package org.herac.tuxguitar.app.editors;

import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGEditorManager {
	
	private TGContext context;
	
	public TGEditorManager(TGContext context){
		this.context = context;
	}
	
	public void doRedraw( int type ){
		TGEventManager.getInstance(this.context).fireEvent(new TGRedrawEvent(type));
	}
	
	public void doUpdate( int type ){
		TGEventManager.getInstance(this.context).fireEvent(new TGUpdateEvent(type));
	}
	
	public void doUpdateMeasure( int number ){
		TGEventManager.getInstance(this.context).fireEvent(new TGUpdateMeasureEvent(number));
	}
	
	public void showExternalBeat( TGBeat beat ){
		TGEventManager.getInstance(this.context).fireEvent(new TGExternalBeatViewerEvent(TGExternalBeatViewerEvent.ACTION_SHOW, beat));
	}
	
	public void hideExternalBeat(){
		TGEventManager.getInstance(this.context).fireEvent(new TGExternalBeatViewerEvent(TGExternalBeatViewerEvent.ACTION_HIDE));
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
	
	public void addBeatViewerListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).addListener(TGExternalBeatViewerEvent.EVENT_TYPE, listener);
	}
	
	public void removeBeatViewerListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).removeListener(TGExternalBeatViewerEvent.EVENT_TYPE, listener);
	}
	
	public static TGEditorManager getInstance(TGContext context) {
		return (TGEditorManager) TGSingletonUtil.getInstance(context, TGEditorManager.class.getName(), new TGSingletonFactory() {
			public Object createInstance(TGContext context) {
				return new TGEditorManager(context);
			}
		});
	}
}