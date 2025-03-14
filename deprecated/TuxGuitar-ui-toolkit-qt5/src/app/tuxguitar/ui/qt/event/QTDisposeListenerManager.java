package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListenerManager;
import app.tuxguitar.ui.qt.QTComponent;
import org.qtjambi.qt.core.QEvent;

public class QTDisposeListenerManager extends UIDisposeListenerManager implements QTEventHandler {

	private QTComponent<?> control;

	public QTDisposeListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public boolean handle(QEvent event) {
		this.onDispose(new UIDisposeEvent(this.control));

		return true;
	}
}
