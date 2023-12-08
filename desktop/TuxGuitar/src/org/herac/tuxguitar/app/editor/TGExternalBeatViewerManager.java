package org.herac.tuxguitar.app.editor;

import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.util.TGAbstractContext;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGExternalBeatViewerManager {
	
	private TGContext context;
	
	public TGExternalBeatViewerManager(TGContext context){
		this.context = context;
	}
	
	public void showExternalBeat(final TGBeat beat){
		this.showExternalBeat(beat, null);
	}
	
	public void showExternalBeat(final TGBeat beat, final TGAbstractContext context){
		this.asyncRunLocked(new Runnable() {
			public void run() {
				doShowExternalBeat(beat, context);
			}
		});
	}
	
	public void hideExternalBeat(){
		this.hideExternalBeat(null);
	}
	
	public void hideExternalBeat(final TGAbstractContext context){
		this.asyncRunLocked(new Runnable() {
			public void run() {
				doHideExternalBeat(context);
			}
		});
	}
	
	public void addBeatViewerListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).addListener(TGExternalBeatViewerEvent.EVENT_TYPE, listener);
	}
	
	public void removeBeatViewerListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).removeListener(TGExternalBeatViewerEvent.EVENT_TYPE, listener);
	}
	
	private void doShowExternalBeat(TGBeat beat, TGAbstractContext context){
		TGEventManager.getInstance(this.context).fireEvent(new TGExternalBeatViewerEvent(TGExternalBeatViewerEvent.ACTION_SHOW, beat, context));
	}
	
	private void doHideExternalBeat(TGAbstractContext context){
		TGEventManager.getInstance(this.context).fireEvent(new TGExternalBeatViewerEvent(TGExternalBeatViewerEvent.ACTION_HIDE, context));
	}
	
	public void asyncRunLocked(final Runnable runnable) {
		TGEditorManager.getInstance(this.context).asyncRunLocked(runnable);
	}
	
	public static TGExternalBeatViewerManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGExternalBeatViewerManager.class.getName(), new TGSingletonFactory<TGExternalBeatViewerManager>() {
			public TGExternalBeatViewerManager createInstance(TGContext context) {
				return new TGExternalBeatViewerManager(context);
			}
		});
	}
}