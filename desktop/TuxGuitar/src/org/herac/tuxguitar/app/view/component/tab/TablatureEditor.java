package org.herac.tuxguitar.app.view.component.tab;

import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.editor.event.TGUpdateMeasuresEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TablatureEditor implements TGEventListener{
	
	private TGContext context;
	private Tablature tablature;
	
	public TablatureEditor(TGContext context) {
		this.context = context;
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
	
	@SuppressWarnings("unchecked")
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.MEASURE_UPDATED ){
			getTablature().updateMeasures((List<Integer>) event.getAttribute(TGUpdateMeasuresEvent.PROPERTY_MEASURE_NUMBERS));
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