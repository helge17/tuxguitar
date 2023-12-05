package org.herac.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.herac.tuxguitar.ui.event.UICloseEvent;
import org.herac.tuxguitar.ui.event.UICloseListenerManager;
import org.herac.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTCloseListenerManager extends UICloseListenerManager implements ShellListener {
	
	private SWTEventReceiver<?> control;
	
	public SWTCloseListenerManager(SWTEventReceiver<?> control) {
		this.control = control;
	}

	public void shellClosed(ShellEvent e) {
		if(!this.control.isIgnoreEvents()) {
			// cancel default operation
			e.doit = false;
			
			this.onClose(new UICloseEvent(this.control));
		}
	}
	
	public void shellActivated(ShellEvent e) {
		// Not implemented
	}

	public void shellDeactivated(ShellEvent e) {
		// Not implemented
	}

	public void shellDeiconified(ShellEvent e) {
		// Not implemented
	}

	public void shellIconified(ShellEvent e) {
		// Not implemented
	}
}
