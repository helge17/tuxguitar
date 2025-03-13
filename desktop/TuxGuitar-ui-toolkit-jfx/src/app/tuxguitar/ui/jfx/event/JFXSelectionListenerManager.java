package app.tuxguitar.ui.jfx.event;

import javafx.event.Event;
import javafx.event.EventHandler;

import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListenerManager;
import app.tuxguitar.ui.jfx.widget.JFXEventReceiver;

public class JFXSelectionListenerManager<T extends Event> extends UISelectionListenerManager implements EventHandler<T> {

	private JFXEventReceiver<?> control;

	public JFXSelectionListenerManager(JFXEventReceiver<?> control) {
		this.control = control;
	}

	public void handle(T event) {
		if(!this.control.isIgnoreEvents()) {
			this.onSelect(new UISelectionEvent(this.control));

			event.consume();
		}
	}
}
