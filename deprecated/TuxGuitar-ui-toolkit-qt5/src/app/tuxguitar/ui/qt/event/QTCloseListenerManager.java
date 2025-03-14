package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UICloseEvent;
import app.tuxguitar.ui.event.UICloseListenerManager;
import app.tuxguitar.ui.qt.QTComponent;
import org.qtjambi.qt.core.QEvent;

public class QTCloseListenerManager extends UICloseListenerManager implements QTEventHandler {

	private QTComponent<?> control;

	public QTCloseListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public QTComponent<?> getControl() {
		return this.control;
	}

	public void handle() {
		this.onClose(new UICloseEvent(this.control));
	}

	public boolean handle(QEvent event) {
		this.handle();

		event.ignore();

		return true;
	}
}
