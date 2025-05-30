package app.tuxguitar.editor;

import java.util.List;

import app.tuxguitar.editor.event.TGDestroyEvent;
import app.tuxguitar.editor.event.TGRedrawEvent;
import app.tuxguitar.editor.event.TGUpdateEvent;
import app.tuxguitar.editor.event.TGUpdateMeasuresEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.event.TGEventManager;
import app.tuxguitar.thread.TGThreadManager;
import app.tuxguitar.util.TGAbstractContext;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGLock;
import app.tuxguitar.util.error.TGErrorManager;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGEditorManager {

	private TGContext context;
	private TGLock lockControl;

	public TGEditorManager(TGContext context){
		this.context = context;
	}

	public void setLockControl(TGLock lockControl) {
		this.lockControl = lockControl;
	}

	public TGLock getLockControl() {
		return lockControl;
	}

	public void redraw(){
		this.redraw(null);
	}

	public void redraw(final TGAbstractContext sourceContext){
		this.asyncRunLocked(new Runnable() {
			public void run() {
				doRedraw(TGRedrawEvent.NORMAL, sourceContext);
			}
		});
	}

	public void redrawPlayingThread(){
		this.redrawPlayingThread(null);
	}

	public void redrawPlayingThread(final TGAbstractContext sourceContext){
		this.asyncRunLocked(new Runnable() {
			public void run() {
				doRedraw(TGRedrawEvent.PLAYING_THREAD, sourceContext);
			}
		});
	}

	public void redrawPlayingNewBeat(){
		this.redrawPlayingNewBeat(null);
	}

	public void redrawPlayingNewBeat(final TGAbstractContext sourceContext){
		this.asyncRunLocked(new Runnable() {
			public void run() {
				doRedraw(TGRedrawEvent.PLAYING_NEW_BEAT, sourceContext);
			}
		});
	}

	public void updateSelection() {
		this.updateSelection(null);
	}

	public void updateSelection(final TGAbstractContext sourceContext) {
		this.asyncRunLocked(new Runnable() {
			public void run() {
				doUpdate(TGUpdateEvent.SELECTION, sourceContext);
			}
		});
	}

	public void updateMeasures(final List<Integer> numbers){
		this.updateMeasures(numbers, null);
	}

	public void updateMeasures(final List<Integer> numbers, final TGAbstractContext sourceContext){
		this.runLocked(new Runnable() {
			public void run() {
				doUpdateMeasures(numbers, sourceContext);
			}
		});
	}

	public void updateSong(){
		this.updateSong(null);
	}

	public void updateSong(final TGAbstractContext sourceContext){
		this.runLocked(new Runnable() {
			public void run() {
				doUpdate(TGUpdateEvent.SONG_UPDATED, sourceContext);
			}
		});
	}

	public void updateSavedSong(){
		this.updateSavedSong(null);
	}

	public void updateSavedSong(final TGAbstractContext sourceContext){
		this.runLocked(new Runnable() {
			public void run() {
				doUpdate(TGUpdateEvent.SONG_SAVED, sourceContext);
			}
		});
	}

	public void updateLoadedSong(){
		this.updateLoadedSong(null);
	}

	public void updateLoadedSong(final TGAbstractContext context){
		this.runLocked(new Runnable() {
			public void run() {
				doUpdate(TGUpdateEvent.SONG_LOADED, context);
			}
		});
	}

	public void destroy(final TGAbstractContext context) {
		this.runLocked(new Runnable() {
			public void run() {
				doDestroy(context);
			}
		});
	}

	public void addRedrawListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).addListener(TGRedrawEvent.EVENT_TYPE, listener);
	}

	public void removeRedrawListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).removeListener(TGRedrawEvent.EVENT_TYPE, listener);
	}

	public void addUpdateListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).addListener(TGUpdateEvent.EVENT_TYPE, listener);
	}

	public void removeUpdateListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).removeListener(TGUpdateEvent.EVENT_TYPE, listener);
	}

	public void addDestroyListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).addListener(TGDestroyEvent.EVENT_TYPE, listener);
	}

	public void removeDestroyListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).removeListener(TGDestroyEvent.EVENT_TYPE, listener);
	}

	private void doRedraw(int type, TGAbstractContext context){
		TGEventManager.getInstance(this.context).fireEvent(new TGRedrawEvent(type, context));
	}

	private void doUpdate(int type, TGAbstractContext context){
		TGEventManager.getInstance(this.context).fireEvent(new TGUpdateEvent(type, context));
	}

	private void doUpdateMeasures(List<Integer> numbers, TGAbstractContext context){
		TGEventManager.getInstance(this.context).fireEvent(new TGUpdateMeasuresEvent(numbers, context));
	}

	private void doDestroy(TGAbstractContext context){
		TGEventManager.getInstance(this.context).fireEvent(new TGDestroyEvent(context));
	}

	public void lock(){
		if( this.lockControl != null ) {
			this.lockControl.lock();
		}
	}

	public boolean tryLock(){
		if( this.lockControl != null ) {
			return this.lockControl.tryLock();
		}
		return true;
	}

	public void unlock(){
		if( this.lockControl != null ) {
			this.lockControl.unlock();
		}
	}

	public boolean isLocked(){
		if( this.lockControl != null ) {
			return this.lockControl.isLocked();
		}
		return false;
	}

	public boolean isUnderLockControl(){
		if( this.lockControl != null ) {
			return this.lockControl.isUnderLockControl();
		}
		return false;
	}

	public void runLocked(Runnable runnable) {
		this.lock();
		try {
			runnable.run();
		} finally {
			this.unlock();
		}
	}

	public void asyncRunLocked(final Runnable runnable) {
		TGThreadManager.getInstance(this.context).start(new Runnable() {
			public void run() {
				try {
					runLocked(runnable);
				} catch (Throwable throwable) {
					TGErrorManager.getInstance(TGEditorManager.this.context).handleError(throwable);
				}
			}
		});
	}

	public static TGEditorManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGEditorManager.class.getName(), new TGSingletonFactory<TGEditorManager>() {
			public TGEditorManager createInstance(TGContext context) {
				return new TGEditorManager(context);
			}
		});
	}
}