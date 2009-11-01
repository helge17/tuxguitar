package org.herac.tuxguitar.cocoa.modifiedmarker;

import org.eclipse.swt.internal.cocoa.NSWindow;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGUpdateListener;

public class ModifiedMarker implements TGUpdateListener {
	
	private boolean enabled;
	
	public ModifiedMarker(){
		super();
	}
	
	/** From 'TGUpdateListener' */
	public void doUpdate( int type ){
	    if (!enabled) return;
	    
	    final boolean isEdited = TuxGuitar.instance().getFileHistory().isUnsavedFile();
	    setFrameState( isEdited );
	}
	
	public void init() throws Throwable{
	    TuxGuitar.instance().getEditorManager().addUpdateListener(this);
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		
		if (!enabled) setFrameState(false);
	}
	
	private void setFrameState(boolean modified) {
	    Shell shell = TuxGuitar.instance().getShell();
   	    NSWindow nsWindow = shell.view.window();
        nsWindow.setDocumentEdited(modified);
	}
	
}
