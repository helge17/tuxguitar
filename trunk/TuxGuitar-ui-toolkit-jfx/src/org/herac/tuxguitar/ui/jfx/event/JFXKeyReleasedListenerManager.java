package org.herac.tuxguitar.ui.jfx.event;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import org.herac.tuxguitar.ui.event.UIKeyEvent;
import org.herac.tuxguitar.ui.event.UIKeyReleasedListenerManager;
import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.jfx.resource.JFXKey;

public class JFXKeyReleasedListenerManager extends UIKeyReleasedListenerManager implements EventHandler<KeyEvent> {
	
	private JFXComponent<?> control;
	
	public JFXKeyReleasedListenerManager(JFXComponent<?> control) {
		this.control = control;
	}
	
	public void handle(KeyEvent event) {
		this.onKeyReleased(new UIKeyEvent(this.control, JFXKey.getConvination(event)));
	}
}
