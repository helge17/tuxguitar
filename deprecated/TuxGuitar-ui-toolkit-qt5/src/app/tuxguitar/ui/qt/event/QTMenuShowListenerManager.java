package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UIMenuEvent;
import app.tuxguitar.ui.event.UIMenuShowListenerManager;
import app.tuxguitar.ui.qt.QTComponent;
import org.qtjambi.qt.core.QEvent;

public class QTMenuShowListenerManager extends UIMenuShowListenerManager implements QTEventHandler, QTSignalHandler {

	private QTComponent<?> control;

	public QTMenuShowListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public void handle() {
		this.onMenuShow(new UIMenuEvent(this.control));
	}

	public boolean handle(QEvent event) {
		this.handle();

		return true;
	}
}
