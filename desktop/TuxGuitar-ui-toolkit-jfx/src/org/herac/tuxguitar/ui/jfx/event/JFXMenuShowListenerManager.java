package org.herac.tuxguitar.ui.jfx.event;

import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

import org.herac.tuxguitar.ui.event.UIMenuEvent;
import org.herac.tuxguitar.ui.event.UIMenuShowListenerManager;
import org.herac.tuxguitar.ui.jfx.widget.JFXEventReceiver;

public class JFXMenuShowListenerManager extends UIMenuShowListenerManager implements EventHandler<WindowEvent> {
	
	private JFXEventReceiver<?> control;
	
	public JFXMenuShowListenerManager(JFXEventReceiver<?> control) {
		this.control = control;
	}
	
	public void handle(WindowEvent event) {
		if(!this.control.isIgnoreEvents()) {
			this.onMenuShow(new UIMenuEvent(this.control));
			
			event.consume();
		}
	}
}
