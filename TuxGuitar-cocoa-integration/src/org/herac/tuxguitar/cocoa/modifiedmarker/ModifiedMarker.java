package org.herac.tuxguitar.cocoa.modifiedmarker;

import org.eclipse.swt.internal.cocoa.NSWindow;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;

public class ModifiedMarker implements TGEventListener {
	
	private boolean enabled;
	
	public ModifiedMarker(){
		super();
	}
	
	public void init() throws Throwable{
	    TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		
		if (!enabled) setFrameState(false);
	}
	
	private void setFrameState(boolean modified) {
	    Shell shell = TuxGuitar.getInstance().getShell();
   	    NSWindow nsWindow = shell.view.window();
        nsWindow.setDocumentEdited(modified);
	}
	
	/** From 'TGEventListener' */
	public void processEvent(TGEvent event) {
		if( this.isEnabled() && TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
		    this.setFrameState(TuxGuitar.getInstance().getFileHistory().isUnsavedFile());
		}
	}
}
