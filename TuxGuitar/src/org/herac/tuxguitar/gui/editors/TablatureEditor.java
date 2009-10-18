/*
 * Created on 30-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.clipboard.ClipBoard;
import org.herac.tuxguitar.gui.editors.tab.Tablature;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TablatureEditor implements TGRedrawListener, TGUpdateListener{
	private Tablature tablature;
	private ClipBoard clipBoard;
	
	public TablatureEditor() {
		this.clipBoard = new ClipBoard();
	}
	
	public void showTablature(Composite parent) {
		this.tablature = new Tablature(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.DOUBLE_BUFFERED);
		this.tablature.initGUI();
		this.tablature.setSongManager(TuxGuitar.instance().getSongManager());
		this.tablature.reloadViewLayout();
		this.tablature.initDefaults();
		this.tablature.updateTablature();
		this.tablature.initCaret();
		this.tablature.setFocus();
		this.initListener();
		this.initKeyActions();
		this.initMenu();
	}
	
	private void initListener(){
		TuxGuitar.instance().getEditorManager().addRedrawListener( this );
		TuxGuitar.instance().getEditorManager().addUpdateListener( this );
	}
	
	private void initKeyActions(){
		TuxGuitar.instance().getkeyBindingManager().appendListenersTo(this.tablature);
	}
	
	private void initMenu(){
		Menu menu = TuxGuitar.instance().getItemManager().getPopupMenu();
		menu.addMenuListener(getTablature().getEditorKit());
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
	
	public void doRedraw(int type) {
		if( type == TGRedrawListener.NORMAL ){
			getTablature().redraw();
		}else if( type == TGRedrawListener.PLAYING_NEW_BEAT ){
			getTablature().redrawPlayingMode();
		}
	}
	
	public void doUpdate(int type) {
		if( type == TGUpdateListener.SONG_UPDATED ){
			getTablature().updateTablature();
		}else if( type == TGUpdateListener.SONG_LOADED ){
			getTablature().updateTablature();
			getTablature().resetScroll();
			getTablature().initCaret();
		}
	}
}