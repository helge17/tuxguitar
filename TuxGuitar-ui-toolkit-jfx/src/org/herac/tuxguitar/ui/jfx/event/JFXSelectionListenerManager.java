package org.herac.tuxguitar.ui.jfx.event;

import javafx.event.Event;
import javafx.event.EventHandler;

import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListenerManager;
import org.herac.tuxguitar.ui.jfx.widget.JFXEventReceiver;

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
