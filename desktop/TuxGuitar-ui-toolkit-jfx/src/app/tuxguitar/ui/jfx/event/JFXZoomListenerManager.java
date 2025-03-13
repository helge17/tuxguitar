package app.tuxguitar.ui.jfx.event;

import app.tuxguitar.ui.event.UIZoomEvent;
import app.tuxguitar.ui.event.UIZoomListenerManager;
import app.tuxguitar.ui.jfx.widget.JFXEventReceiver;

import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;

public class JFXZoomListenerManager extends UIZoomListenerManager implements EventHandler<ScrollEvent> {

	private JFXEventReceiver<?> control;

	public JFXZoomListenerManager(JFXEventReceiver<?> control) {
		this.control = control;
	}

	public void handle(ScrollEvent event) {
		if(!this.control.isIgnoreEvents()) {
			if( event.isControlDown() && event.getDeltaY()!=0) {
				this.onZoom(new UIZoomEvent(this.control, (event.getDeltaY() > 0 ? 1 : -1)));

				event.consume();
			}
		}
	}
}
