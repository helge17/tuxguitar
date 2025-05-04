package app.tuxguitar.ui.jfx.event;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.event.UIMouseMoveListenerManager;
import app.tuxguitar.ui.jfx.resource.JFXMouseButton;
import app.tuxguitar.ui.jfx.widget.JFXEventReceiver;
import app.tuxguitar.ui.resource.UIPosition;

public class JFXMouseMoveListenerManager extends UIMouseMoveListenerManager implements EventHandler<MouseEvent> {

	private JFXEventReceiver<?> control;

	public JFXMouseMoveListenerManager(JFXEventReceiver<?> control) {
		this.control = control;
	}

	public void handle(MouseEvent event) {
		if(!this.control.isIgnoreEvents()) {
			this.onMouseMove(new UIMouseEvent(this.control, new UIPosition((float) event.getX(), (float) event.getY()), JFXMouseButton.getMouseButton(event.getButton()), event.isShiftDown()));

			event.consume();
		}
	}
}
