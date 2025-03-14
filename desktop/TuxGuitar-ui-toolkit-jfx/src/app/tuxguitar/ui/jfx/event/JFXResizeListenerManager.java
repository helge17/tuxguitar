package app.tuxguitar.ui.jfx.event;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import app.tuxguitar.ui.event.UIResizeEvent;
import app.tuxguitar.ui.event.UIResizeListenerManager;
import app.tuxguitar.ui.jfx.widget.JFXEventReceiver;

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
