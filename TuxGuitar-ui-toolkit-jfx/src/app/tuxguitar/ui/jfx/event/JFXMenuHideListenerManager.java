package app.tuxguitar.ui.jfx.event;

import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

import app.tuxguitar.ui.event.UIMenuEvent;
import app.tuxguitar.ui.event.UIMenuHideListenerManager;
import app.tuxguitar.ui.jfx.widget.JFXEventReceiver;

public class JFXMenuHideListenerManager extends UIMenuHideListenerManager implements EventHandler<WindowEvent> {

	private JFXEventReceiver<?> control;

	public JFXMenuHideListenerManager(JFXEventReceiver<?> control) {
		this.control = control;
	}

	public void handle(WindowEvent event) {
		if(!this.control.isIgnoreEvents()) {
			this.onMenuHide(new UIMenuEvent(this.control));

			event.consume();
		}
	}
}
