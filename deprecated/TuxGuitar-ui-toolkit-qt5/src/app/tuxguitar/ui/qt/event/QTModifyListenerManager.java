package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UIModifyEvent;
import app.tuxguitar.ui.event.UIModifyListenerManager;
import app.tuxguitar.ui.qt.QTComponent;
import org.qtjambi.qt.core.QEvent;

public class QTModifyListenerManager extends UIModifyListenerManager implements QTEventHandler, QTSignalHandler {

	private QTComponent<?> control;

	public QTModifyListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public void handle() {
		this.onModify(new UIModifyEvent(this.control));
	}

	public boolean handle(QEvent event) {
		this.handle();

		return true;
	}
}
