package org.herac.tuxguitar.ui.jfx.event;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListenerManager;
import org.herac.tuxguitar.ui.jfx.JFXComponent;

public class JFXSelectionListenerChangeManager<T> extends UISelectionListenerManager implements ChangeListener<T> {
	
	private JFXComponent<?> control;
	
	public JFXSelectionListenerChangeManager(JFXComponent<?> control) {
		this.control = control;
	}
	
	public void fireEvent() {
		this.onSelect(new UISelectionEvent(this.control));
	}
	
	public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
		this.fireEvent();
	}
}
