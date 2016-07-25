package org.herac.tuxguitar.ui.swt.event;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.herac.tuxguitar.ui.event.UIResizeEvent;
import org.herac.tuxguitar.ui.event.UIResizeListenerManager;
import org.herac.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTResizeListenerManager extends UIResizeListenerManager implements Listener {
	
	private SWTEventReceiver<?> control;
	
	public SWTResizeListenerManager(SWTEventReceiver<?> control) {
		this.control = control;
	}
	
	public void handleEvent(Event e) {
		if(!this.control.isIgnoreEvents()) {
			this.onResize(new UIResizeEvent(this.control));
		}
	}
}
