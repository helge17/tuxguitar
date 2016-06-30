package org.herac.tuxguitar.ui.jfx.event;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseExitListenerManager;
import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.jfx.resource.JFXMouseButton;
import org.herac.tuxguitar.ui.resource.UIPosition;

public class JFXMouseExitListenerManager extends UIMouseExitListenerManager implements EventHandler<MouseEvent> {
	
	private JFXComponent<?> control;
	
	public JFXMouseExitListenerManager(JFXComponent<?> control) {
		this.control = control;
	}
	
	public void handle(MouseEvent event) {
		this.onMouseExit(new UIMouseEvent(this.control, new UIPosition((float)event.getX(), (float)event.getY()), JFXMouseButton.getMouseButton(event.getButton())));
	}
}
