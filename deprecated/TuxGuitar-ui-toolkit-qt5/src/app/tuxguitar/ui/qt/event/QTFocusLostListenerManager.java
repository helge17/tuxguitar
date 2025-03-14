package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UIFocusEvent;
import app.tuxguitar.ui.event.UIFocusLostListenerManager;
import app.tuxguitar.ui.qt.QTComponent;
import org.qtjambi.qt.core.QEvent;

public class QTFocusLostListenerManager extends UIFocusLostListenerManager implements QTEventHandler {

	private QTComponent<?> control;

	public QTFocusLostListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public boolean handle(QEvent event) {
		if(!this.control.isDisposed()) {
			this.onFocusLost(new UIFocusEvent(this.control));
		}
		return true;
	}
}
