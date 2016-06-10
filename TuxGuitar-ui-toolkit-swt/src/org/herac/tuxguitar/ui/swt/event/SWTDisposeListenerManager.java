package org.herac.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListenerManager;
import org.herac.tuxguitar.ui.swt.SWTComponent;

public class SWTDisposeListenerManager extends UIDisposeListenerManager implements DisposeListener {
	
	private SWTComponent<?> control;
	
	public SWTDisposeListenerManager(SWTComponent<?> control) {
		this.control = control;
	}

	public void widgetDisposed(DisposeEvent e) {
		this.onDispose(new UIDisposeEvent(this.control));
	}
}
