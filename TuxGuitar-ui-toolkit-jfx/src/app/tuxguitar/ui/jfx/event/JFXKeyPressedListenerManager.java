package app.tuxguitar.ui.jfx.event;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import app.tuxguitar.ui.event.UIKeyEvent;
import app.tuxguitar.ui.event.UIKeyPressedListenerManager;
import app.tuxguitar.ui.jfx.resource.JFXKey;
import app.tuxguitar.ui.jfx.widget.JFXEventReceiver;

public class JFXKeyPressedListenerManager extends UIKeyPressedListenerManager implements EventHandler<KeyEvent> {

	private JFXEventReceiver<?> control;

	public JFXKeyPressedListenerManager(JFXEventReceiver<?> control) {
		this.control = control;
	}

	public void handle(KeyEvent event) {
		if(!this.control.isIgnoreEvents()) {
			this.onKeyPressed(new UIKeyEvent(this.control, JFXKey.getCombination(event)));

			event.consume();
		}
	}
}
