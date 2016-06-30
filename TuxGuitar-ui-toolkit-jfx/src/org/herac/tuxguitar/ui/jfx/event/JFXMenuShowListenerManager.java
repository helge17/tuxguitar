package org.herac.tuxguitar.ui.jfx.event;

import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

import org.herac.tuxguitar.ui.event.UIMenuEvent;
import org.herac.tuxguitar.ui.event.UIMenuShowListenerManager;
import org.herac.tuxguitar.ui.jfx.JFXComponent;

public class JFXMenuShowListenerManager extends UIMenuShowListenerManager implements EventHandler<WindowEvent> {
	
	private JFXComponent<?> control;
	
	public JFXMenuShowListenerManager(JFXComponent<?> control) {
		this.control = control;
	}
	
	public void handle(WindowEvent event) {
		this.onMenuShow(new UIMenuEvent(this.control));
	}
}
