package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UIFocusEvent;
import app.tuxguitar.ui.event.UIFocusGainedListenerManager;
import app.tuxguitar.ui.qt.QTComponent;
import io.qt.core.QEvent;

public class QTFocusGainedListenerManager extends UIFocusGainedListenerManager implements QTEventHandler {

	private QTComponent<?> control;

	public QTFocusGainedListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public boolean handle(QEvent event) {
		this.onFocusGained(new UIFocusEvent(this.control));

		return true;
	}
}
