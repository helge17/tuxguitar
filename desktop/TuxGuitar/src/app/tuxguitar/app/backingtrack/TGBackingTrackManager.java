package app.tuxguitar.app.backingtrack;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

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

	private TGBackingTrackManager(TGContext context) {
		this.context = context;
		this.pendingPlayMs = NO_PENDING;
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
			this.clip.stop();
			this.clip.setFramePosition((int) frameCount);
			this.pendingPlayMs = NO_PENDING;
			return;
		}
		this.clip.stop();
		this.clip.setFramePosition((int) frames);
		this.clip.start();
		this.baseFrames = frames;
		this.baseNanos = System.nanoTime();
		this.playing = true;
		this.pendingPlayMs = NO_PENDING;
	}

	public synchronized void pause() {
		this.pendingPlayMs = NO_PENDING;
		if( this.playing ) {
			this.baseFrames = this.getCurrentFrames();
			this.baseNanos = System.nanoTime();
		}
		if( this.clip != null ) {
			this.clip.stop();
		}
		this.playing = false;
	}

	public synchronized boolean isPlaying() {
		return this.playing && this.clip != null;
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
