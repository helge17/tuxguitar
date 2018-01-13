package org.herac.tuxguitar.ui.jfx.event;

import org.herac.tuxguitar.ui.event.UIZoomEvent;
import org.herac.tuxguitar.ui.event.UIZoomListenerManager;
import org.herac.tuxguitar.ui.jfx.widget.JFXEventReceiver;

import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;

public class JFXZoomListenerManager extends UIZoomListenerManager implements EventHandler<ScrollEvent> {
	
	private JFXEventReceiver<?> control;
	
	public JFXZoomListenerManager(JFXEventReceiver<?> control) {
		this.control = control;
	}
	
	public void handle(ScrollEvent event) {
		if(!this.control.isIgnoreEvents()) {
			if( event.isControlDown() ) {
				this.onZoom(new UIZoomEvent(this.control, (event.getDeltaY() > 0 ? 1 : -1)));
				
				event.consume();
			}
		}
	}
}
