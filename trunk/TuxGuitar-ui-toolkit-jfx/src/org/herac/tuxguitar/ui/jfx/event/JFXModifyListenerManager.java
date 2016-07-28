package org.herac.tuxguitar.ui.jfx.event;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.herac.tuxguitar.ui.event.UIModifyEvent;
import org.herac.tuxguitar.ui.event.UIModifyListenerManager;
import org.herac.tuxguitar.ui.jfx.widget.JFXEventReceiver;

public class JFXModifyListenerManager<T> extends UIModifyListenerManager implements ChangeListener<T> {
	
	private JFXEventReceiver<?> control;
	
	public JFXModifyListenerManager(JFXEventReceiver<?> control) {
		this.control = control;
	}
	
	public void fireEvent() {
		this.onModify(new UIModifyEvent(this.control));
	}
	
	public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
		if(!this.control.isIgnoreEvents()) {
			this.fireEvent();
		}
	}
}
