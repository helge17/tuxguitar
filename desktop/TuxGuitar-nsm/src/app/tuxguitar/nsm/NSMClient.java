package app.tuxguitar.nsm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionInterceptor;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.file.TGExitAction;
import app.tuxguitar.app.action.impl.file.TGReadURLAction;
import app.tuxguitar.app.action.impl.file.TGWriteFileAction;
import app.tuxguitar.app.document.TGDocumentFileManager;
import app.tuxguitar.app.document.TGDocumentListManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.file.TGLoadTemplateAction;
import app.tuxguitar.nsm.osc.OSCMessage;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGException;
import app.tuxguitar.util.TGSynchronizer;
import app.tuxguitar.util.error.TGErrorHandler;
import app.tuxguitar.util.error.TGErrorManager;

/*
 * NSM (Non Session Manager) client.
 *
 * Startup sequence:
 *  1. NSMPlugin.earlyInit() calls startCommunication() — starts the UDP receive
 *     thread and sends /nsm/server/announce.
 *  2. earlyInit() calls waitForSessionDirectory() — blocks briefly (≤ timeout)
 *     until the NSM server sends /nsm/client/open.  The session directory is
 *     returned so the plugin can redirect TuxGuitar's config I/O before any
 *     config file is read.
 *  3. earlyInit() calls activateInterceptor() — registers this object as a
 *     TGActionInterceptor so that the default empty-song load is suppressed and
 *     the session file is opened instead.
 *
 * The NSM session directory (= the path argument of /nsm/client/open) is used
 * as a self-contained folder:
 *   {sessionDir}/tuxguitar.tg             — the song file
 *   {sessionDir}/tuxguitar.cfg            — main application config
 *   {sessionDir}/plugins/                 — per-plugin configs
 *   {sessionDir}/tuxguitar-plugin-settings.cfg — plugin enabled/disabled state
 */
public class NSMClient implements TGActionInterceptor {

	private final TGContext context;
	private final DatagramSocket socket;
	private final InetAddress serverAddress;
	private final int serverPort;

	private Thread receiveThread;
	private volatile boolean running;
	private volatile boolean quitting;

	// Unblocks earlyInit() as soon as /nsm/client/open is received.
	private final CountDownLatch openLatch = new CountDownLatch(1);

	// guarded by synchronized(this)
	private boolean appReady;
	private String pendingFilePath;
	private String sessionFilePath;   // absolute path to the .tg song file

	public NSMClient(TGContext context, String nsmUrl) throws Exception {
		this.context = context;
		this.appReady = false;
		this.pendingFilePath = null;
		this.sessionFilePath = null;
		this.quitting = false;

		// Parse "osc.udp://host:port[/]"
		String spec = nsmUrl.trim();
		if (!spec.startsWith("osc.udp://")) {
			throw new IllegalArgumentException("Unsupported NSM_URL scheme: " + nsmUrl);
		}
		spec = spec.substring("osc.udp://".length());
		if (spec.endsWith("/")) {
			spec = spec.substring(0, spec.length() - 1);
		}
		int colon = spec.lastIndexOf(':');
		String host = spec.substring(0, colon);
		int port = Integer.parseInt(spec.substring(colon + 1));

		this.serverAddress = InetAddress.getByName(host);
		this.serverPort = port;
		this.socket = new DatagramSocket();
	}

	// -------------------------------------------------------------------------
	// Phase 1 — called from earlyInit() before blocking
	// -------------------------------------------------------------------------

	public void startCommunication() {
		this.running = true;
		this.receiveThread = new Thread(new Runnable() {
			public void run() {
				receiveLoop();
			}
		}, "NSM");
		this.receiveThread.setDaemon(true);
		this.receiveThread.start();

		sendAnnounce();
	}

	// Called from NSMPlugin.connect() — after all native plugins (including
	// FluidSynth) have loaded, so our sigaction is the last one installed.
	public void registerNativeSigtermHandler() {
		try {
			final int readFd = NSMSignal.registerSigtermPipe();
			if (readFd < 0) {
				System.err.println("[NSM] registerSigtermPipe() failed — SIGTERM will not be caught");
				return;
			}
			System.out.println("[NSM] native SIGTERM handler registered (pipe read-fd=" + readFd + ")");

			// Wakes up when SIGTERM fires the native handler.
			Thread reader = new Thread(new Runnable() {
				public void run() {
					int rc = NSMSignal.waitSigterm(readFd);
					if (rc == 0) {
						System.err.println("[NSM] SIGTERM received via native pipe — dispatching quit");
						handleQuit();
					} else {
						System.err.println("[NSM] waitSigterm returned " + rc + " (pipe closed or error)");
					}
				}
			}, "NSM-sigterm-reader");
			reader.setDaemon(true);
			reader.start();

			// libjack reinstalls its own SIGTERM sigaction when other JACK clients
			// disconnect (e.g. during session close).  This watchdog reclaims our
			// handler every 100 ms so the window for SIGTERM to slip through is tiny.
			Thread watchdog = new Thread(new Runnable() {
				public void run() {
					while (running) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							break;
						}
						NSMSignal.reinstallSigtermHandler();
					}
				}
			}, "NSM-sigterm-watchdog");
			watchdog.setDaemon(true);
			watchdog.start();

		} catch (UnsatisfiedLinkError e) {
			System.err.println("[NSM] native library not available — SIGTERM will not be caught: " + e);
		}
		// Always register SIGUSR2: the bash wrapper sends USR2 when it receives
		// SIGTERM from the session manager, bypassing JACK's sigaction override.
		registerSigusr2Handler();
	}

	// sun.misc.Signal via reflection — avoids --add-exports and compiles cleanly.
	private void registerSigusr2Handler() {
		try {
			Class<?> signalClass  = Class.forName("sun.misc.Signal");
			Class<?> handlerClass = Class.forName("sun.misc.SignalHandler");
			java.lang.reflect.Method handleMethod =
					signalClass.getMethod("handle", signalClass, handlerClass);
			Object usr2Signal = signalClass.getConstructor(String.class).newInstance("USR2");
			ClassLoader proxyLoader = ClassLoader.getSystemClassLoader();
			Object handler = java.lang.reflect.Proxy.newProxyInstance(
					proxyLoader,
					new Class<?>[]{ handlerClass },
					(proxy, method, args) -> {
						if ("handle".equals(method.getName())) {
							System.err.println("[NSM] SIGUSR2 received — dispatching quit");
							handleQuit();
						}
						return null;
					});
			handleMethod.invoke(null, usr2Signal, handler);
			System.out.println("[NSM] SIGUSR2 handler registered");
		} catch (Throwable t) {
			System.err.println("[NSM] could not register SIGUSR2 handler: " + t);
		}
	}

	// Block until /nsm/client/open is received or the timeout expires.
	// Returns the session directory, or null on timeout.
	public String waitForSessionDirectory(long timeoutMs) {
		try {
			if (openLatch.await(timeoutMs, TimeUnit.MILLISECONDS)) {
				synchronized (this) {
					if (this.sessionFilePath != null) {
						return new File(this.sessionFilePath).getParent();
					}
				}
			} else {
				System.err.println("[NSM] Timed out waiting for /nsm/client/open — config will use default location.");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return null;
	}

	// -------------------------------------------------------------------------
	// Phase 2 — called from earlyInit() after config has been redirected
	// -------------------------------------------------------------------------

	public void activateInterceptor() {
		TGActionManager.getInstance(this.context).addInterceptor(this);
	}


	// -------------------------------------------------------------------------
	// Shutdown
	// -------------------------------------------------------------------------

	public void stop() {
		this.running = false;
		TGActionManager.getInstance(this.context).removeInterceptor(this);
		if (!this.socket.isClosed()) {
			this.socket.close();
		}
	}

	// -------------------------------------------------------------------------
	// Outgoing OSC messages
	// -------------------------------------------------------------------------

	private void sendAnnounce() {
		// Announce the launcher script's PID when available so the session
		// manager sends SIGTERM to the bash wrapper instead of the JVM.
		// The wrapper converts SIGTERM → SIGUSR2 which Java handles cleanly.
		int pid = (int) ProcessHandle.current().pid();
		String launcherPid = System.getenv("NSM_LAUNCHER_PID");
		if (launcherPid != null && !launcherPid.isEmpty()) {
			try {
				pid = Integer.parseInt(launcherPid.trim());
				System.out.println("[NSM] announcing launcher PID " + pid + " (bash wrapper active)");
			} catch (NumberFormatException ignored) {}
		}
		OSCMessage msg = new OSCMessage("/nsm/server/announce");
		msg.addString("TuxGuitar");
		msg.addString("");          // capabilities: none declared
		msg.addString("tuxguitar");
		msg.addInt(1);              // NSM API major version
		msg.addInt(2);              // NSM API minor version
		msg.addInt(pid);
		sendMessage(msg);
	}

	private void sendReply(String path, String message) {
		OSCMessage msg = new OSCMessage("/reply");
		msg.addString(path);
		msg.addString(message);
		sendMessage(msg);
	}

	private void sendError(String path, int code, String message) {
		OSCMessage msg = new OSCMessage("/error");
		msg.addString(path);
		msg.addInt(code);
		msg.addString(message);
		sendMessage(msg);
	}

	private void sendMessage(OSCMessage msg) {
		try {
			byte[] data = msg.encode();
			DatagramPacket packet = new DatagramPacket(data, data.length, this.serverAddress, this.serverPort);
			this.socket.send(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// -------------------------------------------------------------------------
	// Receive loop
	// -------------------------------------------------------------------------

	private void receiveLoop() {
		byte[] buffer = new byte[65536];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		while (this.running) {
			try {
				this.socket.receive(packet);
				OSCMessage msg = OSCMessage.decode(buffer, packet.getLength());
				handleMessage(msg);
			} catch (SocketException e) {
				break; // socket closed, normal shutdown
			} catch (Exception e) {
				TGErrorManager.getInstance(this.context).handleError(e);
			}
		}
	}

	// -------------------------------------------------------------------------
	// Incoming message dispatch
	// -------------------------------------------------------------------------

	private void handleMessage(OSCMessage msg) {
		String addr = msg.getAddress();
		System.out.println("[NSM] received: " + addr);
		if ("/nsm/client/open".equals(addr)) {
			if (msg.argCount() >= 1) {
				handleOpen(msg.getStringArg(0));
			}
		} else if ("/nsm/client/save".equals(addr)) {
			handleSave();
		} else if ("/nsm/client/quit".equals(addr)) {
			System.out.println("[NSM] quit requested — dispatching TGExitAction");
			handleQuit();
		} else if ("/reply".equals(addr) && msg.argCount() >= 2
				&& "/nsm/server/announce".equals(msg.getStringArg(0))) {
			System.out.println("[NSM] session manager: " + msg.getStringArg(1));
		} else if ("/error".equals(addr) && msg.argCount() >= 3) {
			System.err.println("[NSM] error on " + msg.getStringArg(0) + ": " + msg.getStringArg(2));
		}
	}

	// -------------------------------------------------------------------------
	// Open
	// -------------------------------------------------------------------------

	private void handleOpen(String path) {
		// NSM provides a per-client directory path.  All session files live inside it.
		new File(path).mkdirs();
		String filePath = path + File.separator + "tuxguitar.tg";

		boolean readyNow;
		synchronized (this) {
			this.sessionFilePath = filePath;
			readyNow = this.appReady;
			if (readyNow) {
				processOpen(filePath);
			} else {
				this.pendingFilePath = filePath;
			}
		}
		// Unblock earlyInit() so config can be redirected before TuxGuitar reads it.
		openLatch.countDown();
		if (readyNow) {
			// App already started (re-open): reply after the action has been dispatched.
			sendOpenReplyLater();
		}
		// else: reply is deferred to intercept() which fires after startup completes.
	}

	private void sendOpenReplyLater() {
		try {
			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() {
					sendReply("/nsm/client/open", "opened");
				}
			});
		} catch (TGException e) {
			// Synchronizer unavailable — fall back to immediate reply.
			sendReply("/nsm/client/open", "opened");
		}
	}

	private void processOpen(String backupPath) {
		String sessionDir = new File(backupPath).getParent();

		// Prefer the real file recorded by the last NSM save; fall back to the backup.
		String realPath = readPointerFile(sessionDir);
		File targetFile = null;

		if (realPath != null) {
			File realFile = new File(realPath);
			if (realFile.exists()) {
				targetFile = realFile;
				System.out.println("[NSM] opening real file: " + realPath);
			} else {
				System.out.println("[NSM] real file missing (" + realPath + "), falling back to backup");
			}
		}
		if (targetFile == null) {
			File backupFile = new File(backupPath);
			if (backupFile.exists()) {
				targetFile = backupFile;
				System.out.println("[NSM] opening backup: " + backupPath);
			}
		}

		if (targetFile != null) {
			try {
				URL url = targetFile.toURI().toURL();
				TGActionProcessor proc = new TGActionProcessor(this.context, TGReadURLAction.NAME);
				proc.setAttribute(TGReadURLAction.ATTRIBUTE_URL, url);
				proc.process();
			} catch (Exception e) {
				TGErrorManager.getInstance(this.context).handleError(e);
			}
		} else {
			// Brand-new session: load an empty template.
			System.out.println("[NSM] new session, loading empty template");
			TGActionProcessor proc = new TGActionProcessor(this.context, TGLoadTemplateAction.NAME);
			proc.process();
		}
	}

	// -------------------------------------------------------------------------
	// Save
	// -------------------------------------------------------------------------

	private void handleSave() {
		System.out.println("[NSM] save requested");
		final String backupPath;
		synchronized (this) {
			backupPath = this.sessionFilePath;
		}
		if (backupPath == null) {
			System.err.println("[NSM] save failed: no session file path");
			sendError("/nsm/client/save", 1, "no session file path");
			return;
		}
		new File(backupPath).getParentFile().mkdirs();

		// Get the document's current real path (may differ from the session backup).
		final String realPath = getRealDocumentPath();
		System.out.println("[NSM] backup path : " + backupPath);
		System.out.println("[NSM] real path   : " + (realPath != null ? realPath : "(none — new document)"));

		if (realPath != null && !realPath.equals(backupPath)) {
			// Save to the real path first, then write backup and reply.
			saveToPath(realPath, new Runnable() {
				public void run() {
					saveBackupThenReply(backupPath, realPath);
				}
			}, new TGErrorHandler() {
				public void handleError(Throwable e) {
					System.err.println("[NSM] real-path save error (backup will still be written): " + e);
					saveBackupThenReply(backupPath, null);
				}
			});
		} else {
			// New/unsaved document or already working from the session dir.
			saveBackupThenReply(backupPath, null);
		}
		System.out.println("[NSM] save dispatched");
	}

	private void saveToPath(String path, final Runnable onFinish, final TGErrorHandler onError) {
		TGActionProcessor proc = new TGActionProcessor(this.context, TGWriteFileAction.NAME);
		proc.setAttribute(TGWriteFileAction.ATTRIBUTE_FILE_NAME, path);
		proc.setOnFinish(onFinish);
		proc.setErrorHandler(onError);
		proc.process();
	}

	private void saveBackupThenReply(final String backupPath, final String realPath) {
		System.out.println("[NSM] writing backup: " + backupPath);
		TGActionProcessor proc = new TGActionProcessor(this.context, TGWriteFileAction.NAME);
		proc.setAttribute(TGWriteFileAction.ATTRIBUTE_FILE_NAME, backupPath);
		proc.setOnFinish(new Runnable() {
			public void run() {
				// The backup save updated the document URI to the backup path.
				// Restore it to the real path so TuxGuitar keeps working on the right file.
				if (realPath != null) {
					restoreDocumentUri(realPath);
				}
				// Record which real file was open so we can restore it next session.
				writePointerFile(backupPath, realPath);
				System.out.println("[NSM] save onFinish — sending reply");
				try { NSMSignal.reinstallSigtermHandler(); } catch (UnsatisfiedLinkError ignored) {}
				sendReply("/nsm/client/save", "saved");
				System.out.println("[NSM] save reply sent");
			}
		});
		proc.setErrorHandler(new TGErrorHandler() {
			public void handleError(Throwable e) {
				System.err.println("[NSM] backup save error: " + e);
				e.printStackTrace(System.err);
				sendError("/nsm/client/save", 1, e.getMessage() != null ? e.getMessage() : "backup save error");
			}
		});
		proc.process();
	}

	// Returns the full path of the currently open document, or null for a new/unsaved file.
	private String getRealDocumentPath() {
		TGDocumentFileManager fm = TGDocumentFileManager.getInstance(this.context);
		if (fm.isLocalFile()) {
			String dir  = fm.getCurrentFilePath();
			String name = fm.getCurrentFileName(null);
			if (dir != null && name != null) {
				return dir + File.separator + name;
			}
		}
		return null;
	}

	// Restores the document URI after the backup save changed it to the backup path.
	private void restoreDocumentUri(String realPath) {
		try {
			URI uri = new File(realPath).toURI();
			TGDocumentListManager.getInstance(this.context).findCurrentDocument().setUri(uri);
		} catch (Exception e) {
			System.err.println("[NSM] failed to restore document URI: " + e);
		}
	}

	// Writes a one-line pointer file recording which real file is open in this session.
	private void writePointerFile(String backupPath, String realPath) {
		File pointerFile = new File(new File(backupPath).getParent(), "tuxguitar-source.path");
		try {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(pointerFile), "UTF-8"));
			pw.println(realPath != null ? realPath : "");
			pw.close();
		} catch (Exception e) {
			System.err.println("[NSM] failed to write pointer file: " + e);
		}
	}

	// Reads the pointer file; returns the real path or null if absent/empty.
	private String readPointerFile(String sessionDir) {
		File pointerFile = new File(sessionDir, "tuxguitar-source.path");
		if (!pointerFile.exists()) {
			return null;
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pointerFile), "UTF-8"));
			String line = br.readLine();
			br.close();
			return (line != null && !line.trim().isEmpty()) ? line.trim() : null;
		} catch (Exception e) {
			System.err.println("[NSM] failed to read pointer file: " + e);
			return null;
		}
	}

	// -------------------------------------------------------------------------
	// Quit
	// -------------------------------------------------------------------------

	private void handleQuit() {
		if (this.quitting) {
			return;
		}
		this.quitting = true;
		// TGExitAction calls TGWindow.getWindow().close(), which fires the
		// existing window-close listener.  That listener dispatches TGDisposeAction
		// (SAVE_BEFORE), so the "save before exit?" dialog appears when there are
		// unsaved changes — exactly the same path as File > Exit in the menu.
		TGActionProcessor proc = new TGActionProcessor(this.context, TGExitAction.NAME);
		proc.setErrorHandler(new TGErrorHandler() {
			public void handleError(Throwable e) {
				System.err.println("[NSM] quit action failed: " + e);
				e.printStackTrace();
			}
		});
		proc.process();
		System.out.println("[NSM] quit action dispatched");
	}

	// -------------------------------------------------------------------------
	// TGActionInterceptor — suppress default song loading at startup
	// -------------------------------------------------------------------------

	@Override
	public boolean intercept(String id, TGActionContext actionContext) throws TGActionException {
		if (TGLoadTemplateAction.NAME.equals(id)) {
			// One-shot: remove the interceptor after the first startup template dispatch.
			TGActionManager.getInstance(this.context).removeInterceptor(this);
			final String filePath;
			synchronized (this) {
				this.appReady = true;
				filePath = this.pendingFilePath;
				this.pendingFilePath = null;
			}
			if (filePath != null) {
				processOpen(filePath);
				// Reply after the splash screen closes.  executeLater() queues this
				// task AFTER the current event-loop task (startUIContext + splash.finish()),
				// so the session manager only sees us as ready once the UI is fully up.
				sendOpenReplyLater();
			}
			// Suppress the default template load; processOpen() handles the song.
			return true;
		}
		return false;
	}
}
