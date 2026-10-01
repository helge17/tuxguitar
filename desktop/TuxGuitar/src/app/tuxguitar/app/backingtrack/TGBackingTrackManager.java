package app.tuxguitar.app.backingtrack;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import app.tuxguitar.action.TGActionEvent;
import app.tuxguitar.action.TGActionPostExecutionEvent;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.backingtrack.TGAudioFileLoader.AudioData;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.file.TGSetBackingTrackAction;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.thread.TGThreadManager;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.error.TGErrorManager;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGBackingTrackManager implements TGEventListener {

	private static final long NO_PENDING = -1L;

	private TGContext context;
	private Clip clip;
	private AudioFormat format;
	private byte[] pcm;
	private boolean loading;
	private String loadingPath;
	private boolean loaded;
	private String loadedPath;
	private String failedPath;
	private Throwable error;
	private String errorFileName;
	private boolean playing;
	private long baseFrames;
	private long baseNanos;
	private long pendingPlayMs;
	private volatile long lastInternalStopNanos;
	private volatile long repositionNanos;
	private float volume;

	private TGBackingTrackManager(TGContext context) {
		this.context = context;
		this.pendingPlayMs = NO_PENDING;
		this.volume = 1.0f;
	}

	public static TGBackingTrackManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGBackingTrackManager.class.getName(), new TGSingletonFactory<TGBackingTrackManager>() {
			public TGBackingTrackManager createInstance(TGContext context) {
				return new TGBackingTrackManager(context);
			}
		});
	}

	public synchronized void preloadIfNeeded() {
		TGSong song = TGDocumentManager.getInstance(this.context).getSong();
		String path = (song == null) ? null : song.getBackingTrack();
		if( path != null ) {
			path = path.trim();
			if( path.isEmpty() ) {
				path = null;
			}
		}
		if( path == null ) {
			this.releaseInternal();
			return;
		}
		if( path.equals(this.failedPath) ) {
			return;
		}
		if( path.equals(this.loadingPath) && this.loading ) {
			return;
		}
		if( path.equals(this.loadedPath) && this.loaded ) {
			return;
		}
		this.startLoad(path);
	}

	public synchronized void clearFailed() {
		this.failedPath = null;
	}

	private void startLoad(final String path) {
		this.releaseInternal();
		this.failedPath = null;
		this.loading = true;
		this.loadingPath = path;
		TGThreadManager.getInstance(this.context).start(new Runnable() {
			public void run() {
				try {
					AudioData data = TGAudioFileLoader.load(new File(path));
					TGBackingTrackManager.this.onLoaded(path, data);
				} catch (Throwable throwable) {
					TGBackingTrackManager.this.onLoadError(path, throwable);
				}
			}
		});
	}

	private synchronized void onLoaded(String path, AudioData data) {
		if( !this.loading || !path.equals(this.loadingPath) ) {
			return;
		}
		this.loading = false;
		this.loadingPath = null;
		try {
		Clip newClip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, data.getFormat()));
		newClip.addLineListener(new LineListener() {
			public void update(LineEvent event) {
				if( LineEvent.Type.STOP.equals(event.getType()) ) {
					TGBackingTrackManager.this.onClipStoppedSpontaneously();
				}
			}
		});
		newClip.open(data.getFormat(), data.getPcm(), 0, data.getPcm().length);
			newClip.setFramePosition(0);
			this.closeClip();
			this.clip = newClip;
			this.format = data.getFormat();
			this.pcm = data.getPcm();
			this.loaded = true;
			this.loadedPath = path;
			this.failedPath = null;
			this.error = null;
			this.errorFileName = null;
			this.baseFrames = 0;
			this.baseNanos = System.nanoTime();
			this.syncVolumeFromSong();
			this.applyVolume();
			if( this.pendingPlayMs != NO_PENDING && MidiPlayer.getInstance(this.context).isRunning() ) {
				long positionMs = this.pendingPlayMs;
				this.pendingPlayMs = NO_PENDING;
				this.playInternal(positionMs);
			}
		} catch (Throwable throwable) {
			this.closeClip();
			this.loaded = false;
			this.loadedPath = null;
			this.setLoadError(path, throwable);
		}
	}

	private synchronized void onLoadError(String path, Throwable throwable) {
		if( !this.loading || !path.equals(this.loadingPath) ) {
			return;
		}
		this.loading = false;
		this.loadingPath = null;
		this.setLoadError(path, throwable);
	}

	private void setLoadError(String path, Throwable throwable) {
		this.loaded = false;
		this.loadedPath = null;
		this.failedPath = path;
		this.error = throwable;
		this.errorFileName = new File(path).getName();
		this.playing = false;
		this.pendingPlayMs = NO_PENDING;
		if( MidiPlayer.getInstance(this.context).isRunning() ) {
			this.reportError();
		}
	}

	public synchronized void playAt(long positionMs) {
		if( !this.loaded || this.clip == null ) {
			this.pendingPlayMs = positionMs;
			return;
		}
		this.playInternal(positionMs);
	}

	private void playInternal(long positionMs) {
		long frameCount = this.getFrameCount();
		long frames = this.msToFrames(positionMs);
		if( frames >= frameCount ) {
			this.playing = false;
			this.baseFrames = frameCount;
			this.baseNanos = System.nanoTime();
			this.stopClipInternal();
			this.clip.setFramePosition((int) frameCount);
			this.pendingPlayMs = NO_PENDING;
			return;
		}
		// Avoid an audible cut: when already playing and close to the target frame,
		// keep the clip running instead of stop/start (which restarts audio output).
		if( this.playing ) {
			try {
				long currentFrame = this.clip.getLongFramePosition();
				long tolerance = Math.max(1L, (long) (this.format.getFrameRate() * 0.25));
				if( Math.abs(currentFrame - frames) <= tolerance ) {
					this.applyVolume();
					this.pendingPlayMs = NO_PENDING;
					return;
				}
			} catch (Throwable throwable) {
			}
		}
		this.stopClipInternal();
		this.clip.setFramePosition((int) frames);
		this.clip.start();
		this.applyVolume();
		this.baseFrames = frames;
		this.baseNanos = System.nanoTime();
		this.playing = true;
		this.pendingPlayMs = NO_PENDING;
		this.repositionNanos = System.nanoTime();
	}

	/**
	 * Stops the clip as part of an internal operation (reposition or pause).
	 * Records the timestamp so the LineListener ignores the STOP event we cause ourselves.
	 */
	private void stopClipInternal() {
		this.lastInternalStopNanos = System.nanoTime();
		if( this.clip != null ) {
			try {
				this.clip.stop();
			} catch (Throwable throwable) {
			}
		}
	}

	/**
	 * Called from the LineListener when the clip stops on its own (audio underrun
	 * or end of media). Without this, the internal state keeps assuming playback
	 * continues and the position estimate runs away from reality.
	 */
	private synchronized void onClipStoppedSpontaneously() {
		// Ignore STOP events caused by our own stop() calls (repositioning / pause).
		if( System.nanoTime() - this.lastInternalStopNanos < 400000000L ) {
			return;
		}
		Clip clip = this.clip;
		if( clip == null || !this.playing ) {
			return;
		}
		try {
			this.baseFrames = clip.getLongFramePosition();
		} catch (Throwable throwable) {
			this.baseFrames = this.getFrameCount();
		}
		this.baseNanos = System.nanoTime();
		this.playing = false;
	}

	public synchronized void setVolume(float volume) {
		if( volume < 0f ) {
			volume = 0f;
		}
		if( volume > 1f ) {
			volume = 1f;
		}
		this.volume = volume;
		this.applyVolume();
	}

	public synchronized float getVolume() {
		return this.volume;
	}

	private void syncVolumeFromSong() {
		TGSong song = null;
		try {
			song = TGDocumentManager.getInstance(this.context).getSong();
		} catch (Throwable throwable) {
		}
		float volume = (song != null) ? song.getBackingTrackVolume() : 1.0f;
		if( volume < 0f || volume > 1f ) {
			volume = 1.0f;
		}
		this.volume = volume;
	}

	private void applyVolume() {
		Clip clip = this.clip;
		if( clip == null ) {
			return;
		}
		try {
			if( clip.isControlSupported(FloatControl.Type.MASTER_GAIN) ) {
				FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				float dB = (this.volume <= 0.001f) ? gain.getMinimum() : (float) (20.0 * Math.log10(this.volume));
				gain.setValue(dB);
			}
		} catch (Throwable throwable) {
		}
	}

	public synchronized void pause() {
		this.pendingPlayMs = NO_PENDING;
		if( this.playing ) {
			this.baseFrames = this.getCurrentFrames();
			this.baseNanos = System.nanoTime();
		}
		this.stopClipInternal();
		this.playing = false;
	}

	public synchronized boolean isPlaying() {
		return this.playing && this.clip != null;
	}

	/**
	 * True shortly after an internal reposition, when the clip's reported frame
	 * position may still be stale. The sync loop must not "correct" against it,
	 * or it would restart the clip in a loop (audible repeated cuts).
	 */
	public synchronized boolean isInRepositionGrace() {
		return (System.nanoTime() - this.repositionNanos) < 400000000L;
	}

	public synchronized boolean isLoading() {
		return this.loading;
	}

	public synchronized long getPositionMs() {
		if( this.format == null ) {
			return 0L;
		}
		return this.framesToMs(this.getCurrentFrames());
	}

	public synchronized long getDurationMs() {
		if( !this.loaded || this.format == null ) {
			return 0L;
		}
		return this.framesToMs(this.getFrameCount());
	}

	public synchronized boolean hasError() {
		return this.error != null;
	}

	public synchronized String getErrorFileName() {
		return this.errorFileName;
	}

	public synchronized void reportError() {
		if( this.error == null ) {
			return;
		}
		String fileName = this.getErrorFileName();
		if( fileName == null || fileName.trim().isEmpty() ) {
			fileName = String.valueOf(this.error);
		}
		String message = TuxGuitar.getProperty("file.backing-track.error", new String[] {fileName});
		String cause = this.error.getMessage();
		if( cause != null && !cause.trim().isEmpty() ) {
			message = (message + " (" + cause + ")");
		}
		TGErrorManager.getInstance(this.context).handleError(new Exception(message));
	}

	public synchronized void release() {
		this.releaseInternal();
	}

	private void releaseInternal() {
		this.pendingPlayMs = NO_PENDING;
		this.playing = false;
		this.baseFrames = 0;
		this.baseNanos = 0;
		this.closeClip();
		this.pcm = null;
		this.format = null;
		this.loaded = false;
		this.loadedPath = null;
		this.loading = false;
		this.loadingPath = null;
		this.error = null;
		this.errorFileName = null;
	}

	private void closeClip() {
		if( this.clip != null ) {
			try {
				this.clip.stop();
			} catch (Throwable t) {
			}
			try {
				this.clip.close();
			} catch (Throwable t) {
			}
			this.clip = null;
		}
	}

	private long getCurrentFrames() {
		if( !this.playing || this.clip == null || this.format == null ) {
			return this.baseFrames;
		}
		// Prefer the clip's real playback position: it reflects audio underruns/stalls,
		// while a wall-clock estimate would silently drift away from what is audible.
		try {
			long realFrames = this.clip.getLongFramePosition();
			if( realFrames >= 0L ) {
				long frameCount = this.getFrameCount();
				return (realFrames > frameCount) ? frameCount : realFrames;
			}
		} catch (Throwable throwable) {
		}
		// Fallback: wall-clock estimate.
		long elapsedNanos = System.nanoTime() - this.baseNanos;
		long elapsedFrames = (elapsedNanos * (long) this.format.getFrameRate()) / 1000000000L;
		long frames = this.baseFrames + elapsedFrames;
		long frameCount = this.getFrameCount();
		return (frames > frameCount) ? frameCount : frames;
	}

	private long getFrameCount() {
		if( this.pcm == null || this.format == null || this.format.getFrameSize() <= 0 ) {
			return 0L;
		}
		return this.pcm.length / this.format.getFrameSize();
	}

	private long msToFrames(long ms) {
		return ((ms * (long) this.format.getFrameRate()) / 1000L);
	}

	private long framesToMs(long frames) {
		return ((frames * 1000L) / (long) this.format.getFrameRate());
	}

	public void processEvent(TGEvent event) {
		if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			String actionId = (String) event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID);
			if( TGSetBackingTrackAction.NAME.equals(actionId) ) {
				this.clearFailed();
			}
			this.preloadIfNeeded();
		}
	}
}
