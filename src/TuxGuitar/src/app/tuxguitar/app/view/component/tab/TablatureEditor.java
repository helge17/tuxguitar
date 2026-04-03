package app.tuxguitar.app.view.component.tab;

import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.TGEditorManager;
import app.tuxguitar.editor.event.TGUpdateEvent;
import app.tuxguitar.editor.event.TGUpdateMeasuresEvent;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.ui.menu.UIPopupMenu;
import app.tuxguitar.util.TGAbstractContext;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

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
			Boolean updateCaret = true;
			TGAbstractContext eventContext = event.getAttribute(TGEvent.ATTRIBUTE_SOURCE_CONTEXT);
			if (eventContext != null) {
				// defaults to TRUE if undefined in event context
				updateCaret = (!Boolean.FALSE.equals(eventContext.getAttribute(TGUpdateMeasuresEvent.PROPERTY_UPDATE_CARET)));
			}
			getTablature().updateMeasures((List<Integer>) event.getAttribute(TGUpdateMeasuresEvent.PROPERTY_MEASURE_NUMBERS), updateCaret);
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