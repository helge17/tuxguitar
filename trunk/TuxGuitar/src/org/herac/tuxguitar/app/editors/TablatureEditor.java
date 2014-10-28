/*
 * Created on 30-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.clipboard.ClipBoard;
import org.herac.tuxguitar.app.editors.tab.Tablature;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TablatureEditor implements TGEventListener{
	private Tablature tablature;
	private ClipBoard clipBoard;
	
	public TablatureEditor() {
		this.clipBoard = new ClipBoard();
	}
	
	public void showTablature(Composite parent) {
		this.tablature = new Tablature(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.DOUBLE_BUFFERED, TuxGuitar.getInstance().getDocumentManager());
		this.tablature.initGUI();
		this.tablature.reloadViewLayout();
		this.tablature.updateTablature();
		this.tablature.resetCaret();
		this.tablature.setFocus();
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
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getProperty(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.MEASURE_UPDATED ){
			getTablature().updateMeasure(((Integer) event.getProperty(TGUpdateMeasureEvent.PROPERTY_MEASURE_NUMBER)).intValue());
		} else if( type == TGUpdateEvent.SONG_UPDATED ){
			getTablature().updateTablature();
		} else if( type == TGUpdateEvent.SONG_LOADED ){
			getTablature().updateTablature();
			getTablature().resetScroll();
			getTablature().resetCaret();
		}
	}
	
	public void processRedrawEvent(TGEvent event) {
		int type = ((Integer)event.getProperty(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
		if( type == TGRedrawEvent.NORMAL ){
			getTablature().redraw();
		}else if( type == TGRedrawEvent.PLAYING_NEW_BEAT ){
			getTablature().redrawPlayingMode();
		}
	}
	
	public void processEvent(TGEvent event) {
		if( TGRedrawEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processRedrawEvent(event);
		} 
		else if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
	}
}