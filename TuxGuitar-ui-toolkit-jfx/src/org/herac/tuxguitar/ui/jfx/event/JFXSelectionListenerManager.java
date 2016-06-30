package org.herac.tuxguitar.ui.jfx.event;

import javafx.event.Event;
import javafx.event.EventHandler;

import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListenerManager;
import org.herac.tuxguitar.ui.jfx.JFXComponent;

public class JFXSelectionListenerManager<T extends Event> extends UISelectionListenerManager implements EventHandler<T> {
	
	private JFXComponent<?> control;
	
	public JFXSelectionListenerManager(JFXComponent<?> control) {
		this.control = control;
	}
	
	public void handle(T event) {
		this.onSelect(new UISelectionEvent(this.control));
	}
}
