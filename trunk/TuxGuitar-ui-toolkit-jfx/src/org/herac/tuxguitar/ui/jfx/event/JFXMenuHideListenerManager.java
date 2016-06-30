package org.herac.tuxguitar.ui.jfx.event;

import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

import org.herac.tuxguitar.ui.event.UIMenuEvent;
import org.herac.tuxguitar.ui.event.UIMenuHideListenerManager;
import org.herac.tuxguitar.ui.jfx.JFXComponent;

public class JFXMenuHideListenerManager extends UIMenuHideListenerManager implements EventHandler<WindowEvent> {
	
	private JFXComponent<?> control;
	
	public JFXMenuHideListenerManager(JFXComponent<?> control) {
		this.control = control;
	}
	
	public void handle(WindowEvent event) {
		this.onMenuHide(new UIMenuEvent(this.control));
	}
}
