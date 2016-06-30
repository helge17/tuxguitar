package org.herac.tuxguitar.ui.jfx.event;

import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListenerManager;
import org.herac.tuxguitar.ui.jfx.JFXComponent;

public class JFXDisposeListenerManager extends UIDisposeListenerManager {
	
	private JFXComponent<?> control;
	
	public JFXDisposeListenerManager(JFXComponent<?> control) {
		this.control = control;
	}

	public void fireEvent() {
		this.onDispose(new UIDisposeEvent(this.control));
	}
}
