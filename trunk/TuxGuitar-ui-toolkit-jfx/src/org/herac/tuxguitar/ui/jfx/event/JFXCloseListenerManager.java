package org.herac.tuxguitar.ui.jfx.event;

import org.herac.tuxguitar.ui.event.UICloseEvent;
import org.herac.tuxguitar.ui.event.UICloseListenerManager;
import org.herac.tuxguitar.ui.jfx.JFXComponent;

public class JFXCloseListenerManager extends UICloseListenerManager {
	
	private JFXComponent<?> control;
	
	public JFXCloseListenerManager(JFXComponent<?> control) {
		this.control = control;
	}
	
	public void fireEvent() {
		this.onClose(new UICloseEvent(this.control));
	}
}
