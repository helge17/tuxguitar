package app.tuxguitar.ui.jfx.event;

import app.tuxguitar.ui.event.UICloseEvent;
import app.tuxguitar.ui.event.UICloseListenerManager;
import app.tuxguitar.ui.jfx.widget.JFXEventReceiver;

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
