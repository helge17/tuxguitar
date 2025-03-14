package app.tuxguitar.ui.jfx.event;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import app.tuxguitar.ui.event.UIKeyEvent;
import app.tuxguitar.ui.event.UIKeyReleasedListenerManager;
import app.tuxguitar.ui.jfx.resource.JFXKey;
import app.tuxguitar.ui.jfx.widget.JFXEventReceiver;

public class JFXKeyReleasedListenerManager extends UIKeyReleasedListenerManager implements EventHandler<KeyEvent> {

	private JFXEventReceiver<?> control;

	public JFXKeyReleasedListenerManager(JFXEventReceiver<?> control) {
		this.control = control;
	}

	public void handle(KeyEvent event) {
		if(!this.control.isIgnoreEvents()) {
			this.onKeyReleased(new UIKeyEvent(this.control, JFXKey.getCombination(event)));

			event.consume();
		}
	}
}
