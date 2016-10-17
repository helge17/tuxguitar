package org.herac.tuxguitar.editor;

import org.herac.tuxguitar.editor.event.TGDestroyEvent;
import org.herac.tuxguitar.editor.event.TGRedrawEvent;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.editor.event.TGUpdateMeasureEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.util.TGAbstractContext;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGLock;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

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
	
	public void updateMeasure(final int number){
		this.updateMeasure(number, null);
	}
	
	public void updateMeasure(final int number, final TGAbstractContext sourceContext){
		this.runLocked(new Runnable() {
			public void run() {
				doUpdateMeasure(number, sourceContext);
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
	
	private void doUpdateMeasure(int number, TGAbstractContext context){
		TGEventManager.getInstance(this.context).fireEvent(new TGUpdateMeasureEvent(number, context));
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
	
	public void runLocked(Runnable runnable) {
		this.lock();
		try {
			runnable.run();
		} finally {
			this.unlock();
		}
	}
	
	public void asyncRunLocked(final Runnable runnable) {
		new Thread(new Runnable() {
			public void run() {
				try {
					runLocked(runnable);
				} catch (Throwable throwable) {
					TGErrorManager.getInstance(TGEditorManager.this.context).handleError(throwable);
				}
			}
		}).start();
	}

	public static TGEditorManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGEditorManager.class.getName(), new TGSingletonFactory<TGEditorManager>() {
			public TGEditorManager createInstance(TGContext context) {
				return new TGEditorManager(context);
			}
		});
	}
}