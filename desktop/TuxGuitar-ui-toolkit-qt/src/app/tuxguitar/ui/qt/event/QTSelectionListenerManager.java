package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListenerManager;
import app.tuxguitar.ui.qt.QTComponent;
import io.qt.core.QEvent;

public class QTSelectionListenerManager extends UISelectionListenerManager implements QTEventHandler, QTSignalHandler {

	private QTComponent<?> control;

	public QTSelectionListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public QTComponent<?> getControl() {
		return this.control;
	}

	public void handle() {
		this.onSelect(new UISelectionEvent(this.control));
	}

	public boolean handle(QEvent event) {
		this.handle();

		return true;
	}
}
