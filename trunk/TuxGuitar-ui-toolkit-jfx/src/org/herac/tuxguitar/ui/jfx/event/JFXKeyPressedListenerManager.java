package org.herac.tuxguitar.ui.jfx.event;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import org.herac.tuxguitar.ui.event.UIKeyEvent;
import org.herac.tuxguitar.ui.event.UIKeyPressedListenerManager;
import org.herac.tuxguitar.ui.jfx.resource.JFXKey;
import org.herac.tuxguitar.ui.jfx.widget.JFXEventReceiver;

public class JFXKeyPressedListenerManager extends UIKeyPressedListenerManager implements EventHandler<KeyEvent> {
	
	private JFXEventReceiver<?> control;
	
	public JFXKeyPressedListenerManager(JFXEventReceiver<?> control) {
		this.control = control;
	}
	
	public void handle(KeyEvent event) {
		if(!this.control.isIgnoreEvents()) {
			this.onKeyPressed(new UIKeyEvent(this.control, JFXKey.getConvination(event)));
			
			event.consume();
		}
	}
}
