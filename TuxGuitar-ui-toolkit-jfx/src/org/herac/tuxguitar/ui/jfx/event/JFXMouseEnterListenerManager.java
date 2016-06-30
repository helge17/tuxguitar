package org.herac.tuxguitar.ui.jfx.event;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import org.herac.tuxguitar.ui.event.UIMouseEnterListenerManager;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.jfx.resource.JFXMouseButton;
import org.herac.tuxguitar.ui.resource.UIPosition;

public class JFXMouseEnterListenerManager extends UIMouseEnterListenerManager implements EventHandler<MouseEvent> {
	
	private JFXComponent<?> control;
	
	public JFXMouseEnterListenerManager(JFXComponent<?> control) {
		this.control = control;
	}
	
	public void handle(MouseEvent event) {
		this.onMouseEnter(new UIMouseEvent(this.control, new UIPosition((float)event.getX(), (float)event.getY()), JFXMouseButton.getMouseButton(event.getButton())));
	}
}
