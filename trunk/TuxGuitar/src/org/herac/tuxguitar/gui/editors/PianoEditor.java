package org.herac.tuxguitar.gui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.piano.Piano;
import org.herac.tuxguitar.gui.system.icons.IconLoader;
import org.herac.tuxguitar.gui.system.language.LanguageLoader;
import org.herac.tuxguitar.gui.util.DialogUtils;

public class PianoEditor implements TGRedrawListener, IconLoader,LanguageLoader{
	
	private Piano piano;
	
	public PianoEditor(){
		super();
	}
	
	public void show() {
		Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("piano.editor"));
		
		this.piano = new Piano(dialog,SWT.NONE);
		
		this.addListeners();
		dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				removeListeners();
				TuxGuitar.instance().updateCache(true);
			}
		});
		DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	public void addListeners(){
		TuxGuitar.instance().getIconManager().addLoader(this);
		TuxGuitar.instance().getLanguageManager().addLoader(this);
		TuxGuitar.instance().getEditorManager().addRedrawListener(this);
	}
	
	public void removeListeners(){
		TuxGuitar.instance().getIconManager().removeLoader(this);
		TuxGuitar.instance().getLanguageManager().removeLoader(this);
		TuxGuitar.instance().getEditorManager().removeRedrawListener(this);
	}
	
	private Piano getPiano(){
		return this.piano;
	}
	
	public void setScaleChanges(){
		if(!isDisposed()){
			getPiano().setChanges(true);
		}
	}
	
	public void dispose(){
		if(!isDisposed()){
			getPiano().getShell().dispose();
			getPiano().dispose();
		}
	}
	
	public void redraw(){
		if(!isDisposed() && !TuxGuitar.instance().isLocked()){
			getPiano().redraw();
		}
	}
	
	public void redrawPlayingMode(){
		if(!isDisposed() && !TuxGuitar.instance().isLocked()){
			getPiano().redrawPlayingMode();
		}
	}
	
	public boolean isDisposed() {
		return (this.getPiano() == null || getPiano().isDisposed());
	}
	
	public void loadProperties(){
		if(!isDisposed()){
			getPiano().loadProperties();
			getPiano().getShell().setText(TuxGuitar.getProperty("piano.editor"));
		}
	}
	
	public void loadIcons(){
		if(!isDisposed()){
			getPiano().loadIcons();
		}
	}

	public void doRedraw(int type) {
		if( type == TGRedrawListener.NORMAL ){
			this.redraw();
		}else if( type == TGRedrawListener.PLAYING_NEW_BEAT ){
			this.redrawPlayingMode();
		}
	}
}
