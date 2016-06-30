package org.herac.tuxguitar.ui.jfx.event;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.herac.tuxguitar.ui.event.UIResizeEvent;
import org.herac.tuxguitar.ui.event.UIResizeListenerManager;
import org.herac.tuxguitar.ui.jfx.JFXComponent;

public class JFXResizeListenerManager extends UIResizeListenerManager implements ChangeListener<Number> {
	
	private JFXComponent<?> control;
	
	public JFXResizeListenerManager(JFXComponent<?> control) {
		this.control = control;
	}
	
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		this.onResize(new UIResizeEvent(this.control));
	}
}
