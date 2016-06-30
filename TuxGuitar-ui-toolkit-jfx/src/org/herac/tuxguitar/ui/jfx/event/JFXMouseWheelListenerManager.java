package org.herac.tuxguitar.ui.jfx.event;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;

import org.herac.tuxguitar.ui.event.UIMouseWheelEvent;
import org.herac.tuxguitar.ui.event.UIMouseWheelListenerManager;
import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.jfx.resource.JFXMouseButton;
import org.herac.tuxguitar.ui.resource.UIPosition;

public class JFXMouseWheelListenerManager extends UIMouseWheelListenerManager implements EventHandler<ScrollEvent> {
	
	private JFXComponent<?> control;
	
	public JFXMouseWheelListenerManager(JFXComponent<?> control) {
		this.control = control;
	}
	
	public void handle(ScrollEvent event) {
		Integer button = JFXMouseButton.getMouseButton(MouseButton.MIDDLE);
		Integer value = (int) Math.round(event.getDeltaX() != 0 ? event.getDeltaX() : event.getDeltaY());
		this.onMouseWheel(new UIMouseWheelEvent(this.control, new UIPosition((float)event.getX(), (float)event.getY()), button, value));
	}
}
