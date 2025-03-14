package app.tuxguitar.ui.jfx.event;

import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListenerManager;
import app.tuxguitar.ui.jfx.widget.JFXEventReceiver;

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
