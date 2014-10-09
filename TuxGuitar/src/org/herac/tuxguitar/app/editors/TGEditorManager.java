package org.herac.tuxguitar.app.editors;

import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.song.models.TGBeat;

public class TGEditorManager {
	
	private TGEditorContext editorContext;
	
	public TGEditorManager(){
		this.editorContext = new TGEditorContext();
	}
	
	public TGEditorContext getActiveContext() {
		return this.editorContext;
	}
	
	public void doRedraw( int type ){
		TGEventManager.getInstance().fireEvent(new TGRedrawEvent(type));
	}
	
	public void doUpdate( int type ){
		TGEventManager.getInstance().fireEvent(new TGUpdateEvent(type));
	}
	
	public void doUpdateMeasure( int number ){
		TGEventManager.getInstance().fireEvent(new TGUpdateMeasureEvent(number));
	}
	
	public void showExternalBeat( TGBeat beat ){
		TGEventManager.getInstance().fireEvent(new TGExternalBeatViewerEvent(TGExternalBeatViewerEvent.ACTION_SHOW, beat));
	}
	
	public void hideExternalBeat(){
		TGEventManager.getInstance().fireEvent(new TGExternalBeatViewerEvent(TGExternalBeatViewerEvent.ACTION_HIDE));
	}
	
	public void addRedrawListener(TGEventListener listener){
		TGEventManager.getInstance().addListener(TGRedrawEvent.EVENT_TYPE, listener);
	}
	
	public void removeRedrawListener(TGEventListener listener){
		TGEventManager.getInstance().removeListener(TGRedrawEvent.EVENT_TYPE, listener);
	}
	
	public void addUpdateListener(TGEventListener listener){
		TGEventManager.getInstance().addListener(TGUpdateEvent.EVENT_TYPE, listener);
	}
	
	public void removeUpdateListener(TGEventListener listener){
		TGEventManager.getInstance().removeListener(TGUpdateEvent.EVENT_TYPE, listener);
	}
	
	public void addBeatViewerListener(TGEventListener listener){
		TGEventManager.getInstance().addListener(TGExternalBeatViewerEvent.EVENT_TYPE, listener);
	}
	
	public void removeBeatViewerListener(TGEventListener listener){
		TGEventManager.getInstance().removeListener(TGExternalBeatViewerEvent.EVENT_TYPE, listener);
	}
}