package org.herac.tuxguitar.app.view.component.tab;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.clipboard.ClipBoard;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.editor.event.TGUpdateMeasureEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TablatureEditor implements TGEventListener{
	
	private TGContext context;
	private Tablature tablature;
	private ClipBoard clipBoard;
	
	public TablatureEditor(TGContext context) {
		this.context = context;
		this.clipBoard = new ClipBoard();
		
		this.initialize();
	}
	
	public void initialize() {
		this.tablature = new Tablature(this.context, TGDocumentManager.getInstance(this.context));
		this.tablature.reloadViewLayout();
		this.tablature.updateTablature();
		this.tablature.resetCaret();
		
		this.initListener();
		this.initMenu();
	}
	
	private void initListener(){
		TGEditorManager.getInstance(this.context).addUpdateListener( this );
	}
	
	private void initMenu(){
		UIPopupMenu uiPopupMenu = TuxGuitar.getInstance().getItemManager().getPopupMenu();
		uiPopupMenu.addMenuShowListener(getTablature().getEditorKit().getMouseKit());
		uiPopupMenu.addMenuHideListener(getTablature().getEditorKit().getMouseKit());
	}
	
	public void reloadConfig(){
		getTablature().reloadStyles();
	}
	
	public Tablature getTablature() {
		return this.tablature;
	}
	
	public ClipBoard getClipBoard(){
		return this.clipBoard;
	}
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.MEASURE_UPDATED ){
			getTablature().updateMeasure(((Integer) event.getAttribute(TGUpdateMeasureEvent.PROPERTY_MEASURE_NUMBER)).intValue());
		} else if( type == TGUpdateEvent.SONG_UPDATED ){
			getTablature().updateTablature();
		} else if( type == TGUpdateEvent.SONG_LOADED ){
			getTablature().updateTablature();
		}
	}
	
	public void processEvent(final TGEvent event) { 
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
	}
	
	public static TablatureEditor getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TablatureEditor.class.getName(), new TGSingletonFactory<TablatureEditor>() {
			public TablatureEditor createInstance(TGContext context) {
				return new TablatureEditor(context);
			}
		});
	}
}