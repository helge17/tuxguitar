package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UIResizeEvent;
import app.tuxguitar.ui.event.UIResizeListenerManager;
import app.tuxguitar.ui.qt.QTComponent;
import io.qt.core.QEvent;

public class QTResizeListenerManager extends UIResizeListenerManager implements QTEventHandler {

	private QTComponent<?> control;

	public QTResizeListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public boolean handle(QEvent event) {
		this.onResize(new UIResizeEvent(this.control));

		return true;
	}
}
