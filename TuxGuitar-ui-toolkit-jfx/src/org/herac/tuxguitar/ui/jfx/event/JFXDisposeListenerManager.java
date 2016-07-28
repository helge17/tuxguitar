package org.herac.tuxguitar.ui.jfx.event;

import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListenerManager;
import org.herac.tuxguitar.ui.jfx.widget.JFXEventReceiver;

public class JFXDisposeListenerManager extends UIDisposeListenerManager {
	
	private JFXEventReceiver<?> control;
	
	public JFXDisposeListenerManager(JFXEventReceiver<?> control) {
		this.control = control;
	}

	public void fireEvent() {
		if(!this.control.isIgnoreEvents()) {
			this.onDispose(new UIDisposeEvent(this.control));
		}
	}
}
