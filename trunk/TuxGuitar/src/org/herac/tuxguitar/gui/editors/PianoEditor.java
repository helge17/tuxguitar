package org.herac.tuxguitar.gui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.piano.Piano;
import org.herac.tuxguitar.gui.system.icons.IconLoader;
import org.herac.tuxguitar.gui.system.language.LanguageLoader;
import org.herac.tuxguitar.gui.system.lock.TGSongLock;
import org.herac.tuxguitar.gui.util.DialogUtils;

public class PianoEditor implements IconLoader,LanguageLoader{
	
	private Piano piano;
	
	public PianoEditor(){
		super();
		TuxGuitar.instance().getIconManager().addLoader(this);
		TuxGuitar.instance().getLanguageManager().addLoader(this);
	}

    public void show() {
		Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM);		
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("piano.editor"));
		
		this.piano = new Piano(dialog,SWT.NONE);
		
		TuxGuitar.instance().updateCache(true);		
		
		DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);		
		
		TuxGuitar.instance().updateCache(true);
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
		if(!isDisposed() && !TGSongLock.isLocked()){
			getPiano().redraw();
		}
	}
	
	public void redrawPlayingMode(){
		if(!isDisposed() && !TGSongLock.isLocked()){
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
	
}
