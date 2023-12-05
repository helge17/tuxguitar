package org.herac.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListenerManager;
import org.herac.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTDisposeListenerManager extends UIDisposeListenerManager implements DisposeListener {
	
	private SWTEventReceiver<?> control;
	
	public SWTDisposeListenerManager(SWTEventReceiver<?> control) {
		this.control = control;
	}

	public void widgetDisposed(DisposeEvent e) {
		if(!this.control.isIgnoreEvents()) {
			this.onDispose(new UIDisposeEvent(this.control));
		}
	}
}
