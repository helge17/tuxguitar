package org.herac.tuxguitar.app.view.component.tab;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.clipboard.ClipBoard;
import org.herac.tuxguitar.editor.event.TGRedrawEvent;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.editor.event.TGUpdateMeasureEvent;
import org.herac.tuxguitar.app.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TablatureEditor implements TGEventListener{
	
	private TGContext context;
	private Tablature tablature;
	private ClipBoard clipBoard;
	private TGSyncProcessLocked redrawProcess;
	private TGSyncProcessLocked redrawPlayModeProcess;
	
	public TablatureEditor(TGContext context) {
		this.context = context;
		this.clipBoard = new ClipBoard();
	}
	
	public void showTablature(Composite parent) {
		this.tablature = new Tablature(this.context, parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.DOUBLE_BUFFERED, TuxGuitar.getInstance().getDocumentManager());
		this.tablature.initGUI();
		this.tablature.reloadViewLayout();
		this.tablature.updateTablature();
		this.tablature.resetCaret();
		this.tablature.setFocus();
		this.createSyncProcesses();
		this.initListener();
		this.initKeyActions();
		this.initMenu();
	}
	
	private void initListener(){
		TuxGuitar.getInstance().getEditorManager().addRedrawListener( this );
		TuxGuitar.getInstance().getEditorManager().addUpdateListener( this );
	}
	
	private void initKeyActions(){
		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.tablature);
	}
	
	private void initMenu(){
		Menu menu = TuxGuitar.getInstance().getItemManager().getPopupMenu();
		menu.addMenuListener(getTablature().getEditorKit().getMenuListener());
		this.tablature.setMenu(menu);
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
	
	public boolean isDisposed() {
		return (this.tablature == null || this.tablature.isDisposed());
	}
	
	public void createSyncProcesses() {
		this.redrawProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				getTablature().redraw();
			}
		});
		
		this.redrawPlayModeProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				getTablature().redrawPlayingMode();
			}
		});
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
	
	public void processRedrawEvent(final TGEvent event) {
		int type = ((Integer)event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
		if( type == TGRedrawEvent.NORMAL ){
			this.redrawProcess.process();
		}else if( type == TGRedrawEvent.PLAYING_NEW_BEAT ){
			this.redrawPlayModeProcess.process();
		}
	}
	
	public void processEvent(final TGEvent event) {
		if( TGRedrawEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processRedrawEvent(event);
		} 
		else if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
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