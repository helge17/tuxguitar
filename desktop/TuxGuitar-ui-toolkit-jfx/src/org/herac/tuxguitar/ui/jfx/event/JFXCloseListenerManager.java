package org.herac.tuxguitar.ui.jfx.event;

import org.herac.tuxguitar.ui.event.UICloseEvent;
import org.herac.tuxguitar.ui.event.UICloseListenerManager;
import org.herac.tuxguitar.ui.jfx.widget.JFXEventReceiver;

public class JFXCloseListenerManager extends UICloseListenerManager {
	
	private JFXEventReceiver<?> control;
	
	public JFXCloseListenerManager(JFXEventReceiver<?> control) {
		this.control = control;
	}
	
	public void fireEvent() {
		if(!this.control.isIgnoreEvents()) {
			this.onClose(new UICloseEvent(this.control));
		}
	}
}
