package org.herac.tuxguitar.ui.jfx.event;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import org.herac.tuxguitar.ui.event.UIKeyEvent;
import org.herac.tuxguitar.ui.event.UIKeyPressedListenerManager;
import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.jfx.resource.JFXKey;

public class JFXKeyPressedListenerManager extends UIKeyPressedListenerManager implements EventHandler<KeyEvent> {
	
	private JFXComponent<?> control;
	
	public JFXKeyPressedListenerManager(JFXComponent<?> control) {
		this.control = control;
	}
	
	public void handle(KeyEvent event) {
		this.onKeyPressed(new UIKeyEvent(this.control, JFXKey.getConvination(event)));
	}
}
