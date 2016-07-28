package org.herac.tuxguitar.ui.jfx.event;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.herac.tuxguitar.ui.event.UIResizeEvent;
import org.herac.tuxguitar.ui.event.UIResizeListenerManager;
import org.herac.tuxguitar.ui.jfx.widget.JFXEventReceiver;

public class JFXResizeListenerManager extends UIResizeListenerManager implements ChangeListener<Number> {
	
	private JFXEventReceiver<?> control;
	
	public JFXResizeListenerManager(JFXEventReceiver<?> control) {
		this.control = control;
	}
	
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		if(!this.control.isIgnoreEvents()) {
			this.onResize(new UIResizeEvent(this.control));
		}
	}
}
