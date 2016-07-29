package org.herac.tuxguitar.ui.jfx.event;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import org.herac.tuxguitar.ui.event.UIKeyEvent;
import org.herac.tuxguitar.ui.event.UIKeyReleasedListenerManager;
import org.herac.tuxguitar.ui.jfx.resource.JFXKey;
import org.herac.tuxguitar.ui.jfx.widget.JFXEventReceiver;

public class JFXKeyReleasedListenerManager extends UIKeyReleasedListenerManager implements EventHandler<KeyEvent> {
	
	private JFXEventReceiver<?> control;
	
	public JFXKeyReleasedListenerManager(JFXEventReceiver<?> control) {
		this.control = control;
	}
	
	public void handle(KeyEvent event) {
		if(!this.control.isIgnoreEvents()) {
			this.onKeyReleased(new UIKeyEvent(this.control, JFXKey.getConvination(event)));
			
			event.consume();
		}
	}
}
