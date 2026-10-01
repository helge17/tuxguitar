# 伴奏轨（Backing Track）功能“伪实现”排查与修复计划

> 仓库：yukitakasama/tuxguitar（fork）
> 涉及提交：`6afa03dc9 Add backing track support (#735)`（分支 `backing-track`）
> 现象：菜单“文件 → 选择伴奏轨 / 移除伴奏轨”按钮存在，选择音频文件后无报错，但播放时**听不到任何伴奏**。

---

## 1. 一句话根因（Root Cause）

核心动作 **`TGSetBackingTrackAction`**——真正把伴奏文件路径写入歌曲对象（`song.setBackingTrack(path)`）的动作——**从未被 `installAction()` 注册到 `TGActionManager`**。

`TGActionManager.execute()` 在动作未注册时会**静默返回**（见 `common/TuxGuitar-lib/.../action/TGActionManager.java:60`）：

```java
public void execute(String id, TGActionContext context) {
    TGAction action = getAction(id);
    if( action != null ){                 // ← 未注册时为 null，直接跳过
        ... action.execute(context); ...
    }
    // 否则什么都不做，也不抛异常
}
```

因此所有对它的派发都是空操作，歌曲始终 `backingTrack == null`，加载/播放链路全部失效。

---

## 2. 调用链与证据

### 2.1 预期调用链
```
菜单“选择伴奏轨”
  └─ TGSelectBackingTrackAction（已注册✅）
       └─ 文件选择回调
            └─ TGActionProcessor.processOnNewThread()
                 └─ TGActionManager.execute("action.file.set-backing-track", ctx)
                      └─ TGSetBackingTrackAction.processAction()   ← 应执行 song.setBackingTrack(path)
                           └─ 写入 song.getBackingTrack() != null
                                └─ TGBackingTrackManager.preloadIfNeeded() 加载音频
                                     └─ MidiPlayer 播放时 TGBackingTrackListener 触发 Clip.start()
```

### 2.2 实际断点
- `TGSetBackingTrackAction.NAME = "action.file.set-backing-track"` 在 `TGActionConfigMap` 中**只配置了 flags**（`map(..., LOCKABLE | STOP_TRANSPORT, UPDATE_SONG_CTL, new TGUndoableBackingTrackController())`），**没有注册实例**。
- 全仓 `grep -rn "new TGSetBackingTrackAction"` 结果：**0 处**。
- `desktop/.../action/installer/TGActionInstaller.java` 的 `installActions()` 只安装了
  `TGSelectBackingTrackAction` 与 `TGRemoveBackingTrackAction`，**没有安装 `TGSetBackingTrackAction`**。
- `installAction()` 才是真正把实例塞进 `TGActionManager.actions` map 的地方
  （`TGActionInstaller.java:594` → `TGActionManager.mapAction(actionId, action)`）。
- 而 `TGActionConfigMap.map()` 只写 `configMap`（配置标志），**不写 `actions` 实例表**。

### 2.3 连锁失效
- 选择文件 → 派发 `set-backing-track` → `execute` 静默空操作 → `song.setBackingTrack(path)` 永不调用。
- `TGBackingTrackManager.preloadIfNeeded()` 读 `song.getBackingTrack()` 为 `null` → `releaseInternal()`，不加载任何音频。
- 播放时 `TGBackingTrackListener` 拿到 `durationMs == 0`，`playAt` 不会启动 Clip → **无声**。
- “移除伴奏轨”(`TGRemoveBackingTrackAction`) 与撤销/重做(`TGUndoableBackingTrack`) 同样都派发这个**未注册**动作 → 同样无效。

> 结论：前端按钮可见、流程看似完整，但数据层（把路径写进 song）这一步从一开始就没接通——这就是“伪实现”的本质。

---

## 3. 次要问题（健壮性，建议一并处理）

| # | 问题 | 位置 | 说明 |
|---|------|------|------|
| S1 | `jlayer` 依赖作用域为 `provided` | `desktop/TuxGuitar/pom.xml` | `TGAudioFileLoader.loadMp3` 直接引用 `javazoom.jl.decoder.*`，运行期被本模块使用，应是 `compile`（默认）作用域。当前仅靠发布脚本把 `jlayer.jar` 拷到 `lib/` 且启动脚本 glob `lib/*` 才“碰巧”可用；若以 `java -jar` / `mvn exec:java` 等非 glob 方式运行，MP3 解码会抛 `NoClassDefFoundError` 并被 `startLoad` 的 catch 吞掉 → 首次选 MP3 静默失败。 |
| S2 | 加载失败上报存在竞态 | `TGBackingTrackManager` / `TGBackingTrackListener` | 首次加载失败时，因 `handleNotification` 在 `onLoadError` 之前已通过 `hasError()` 检查，错误不会弹窗；需第二次播放才会提示。建议加载失败立即上报并清理 `pendingPlayMs`。 |
| S3 | `clip.setFramePosition((int) frames)` 强转溢出 | `TGBackingTrackManager.playInternal` | 长曲目/跳转时 `frames` 超 `int` 上限（约 13.5h @44.1kHz）会溢出为负。建议用 `long` 处理或分段 seek。 |

---

## 4. 修复计划（Step by Step）

### Step 1（必须）— 注册核心动作
文件：`desktop/TuxGuitar/src/app/tuxguitar/app/action/installer/TGActionInstaller.java`

1. 新增 import：
   ```java
   import app.tuxguitar.editor.action.file.TGSetBackingTrackAction;
   ```
2. 在 `installActions()` 中、`TGSelectBackingTrackAction` / `TGRemoveBackingTrackAction` 附近新增：
   ```java
   installAction(new TGSetBackingTrackAction(context));
   ```
   （该模块已依赖 `TuxGuitar-editor-utils`，`TGActionConfigMap` 已引用同包类，无需新增依赖。）

### Step 2（建议，紧随）— 修正 jlayer 作用域
文件：`desktop/TuxGuitar/pom.xml`
```diff
- <artifactId>jlayer</artifactId>
- <scope>provided</scope>
+ <artifactId>jlayer</artifactId>
+ <!-- 默认 compile 作用域，确保运行期一定可用；依赖管理已锁定 1.0.1 -->
```
（依赖管理 `desktop/pom.xml` 中 `javazoom:jlayer:1.0.1` 已存在，无需改版本。）

### Step 3（可选，健壮性）
- S2：在 `onLoadError`/`setLoadError` 中，若 `pendingPlayMs != NO_PENDING` 则立即 `reportError()` 并清 `pendingPlayMs`。
- S3：`playInternal` 中 `setFramePosition` 改用安全计算（或限制最大可 seek 位置）。

### Step 4（验证）
- **编译**：`mvn -pl desktop/TuxGuitar -am compile`
- **功能**：运行 → 文件 → 选择伴奏轨 → 分别选一个 **WAV** 和一个 **MP3** → 播放，应能同步听到伴奏；
  停止/循环/变速（非 100%）时应暂停或重新对齐；文件 → 移除伴奏轨 应停止且不再引用路径。
- **持久化回归**：保存 `.tg` 后重新打开，伴奏路径应被 `TGSongReaderImpl` 读回并在播放时加载。
- **撤销/重做**：设置/移除伴奏轨应可被 undo/redo，且无 `action not found` 异常。
- **单测建议**：
  - `TGAudioFileLoader.load(File)` 对 WAV/MP3 解析断言。
  - `TGSetBackingTrackAction` 执行后断言 `song.getBackingTrack().equals(path)`。

---

## 5. 影响面与风险

- Step 1 仅**新增一个已存在动作的注册**，不改变其它动作行为；风险极低。
- 该动作已被配置 `STOP_TRANSPORT | UPDATE_SONG_CTL` 及 `TGUndoableBackingTrackController`，注册后即自动享有：选择/移除时先停播放、更新歌曲、入撤销栈——均为预期行为。
- Step 2 把 `provided` 改为 `compile`，仅扩大运行期 classpath，不影响编译结果。

---

## 6. 关键文件清单

| 文件 | 角色 |
|------|------|
| `common/TuxGuitar-editor-utils/.../editor/action/file/TGSetBackingTrackAction.java` | 真正写 `song.setBackingTrack()` 的动作（**未注册**） |
| `desktop/.../action/installer/TGActionInstaller.java` | 动作注册入口（**遗漏安装**） |
| `desktop/.../action/installer/TGActionConfigMap.java` | 仅配置 flags，不注册实例 |
| `desktop/.../action/impl/file/TGSelectBackingTrackAction.java` | 选择文件并派发 `set-backing-track` |
| `desktop/.../action/impl/file/TGRemoveBackingTrackAction.java` | 移除并派发 `set-backing-track` |
| `desktop/.../backingtrack/TGBackingTrackManager.java` | 加载/播放 Clip、post-exec 监听 |
| `desktop/.../backingtrack/TGBackingTrackListener.java` | 监听 MidiPlayer 事件并触发播放 |
| `desktop/.../backingtrack/TGAudioFileLoader.java` | 解码 WAV/MP3 为 PCM（依赖 jlayer） |
| `desktop/TuxGuitar/pom.xml` | jlayer 作用域问题（S1） |
| `common/TuxGuitar-lib/.../action/TGActionManager.java` | `execute()` 未注册动作静默返回（根因放大点） |
