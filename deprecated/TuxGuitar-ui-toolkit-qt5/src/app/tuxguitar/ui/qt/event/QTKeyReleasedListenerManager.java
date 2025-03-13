package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UIKeyEvent;
import app.tuxguitar.ui.event.UIKeyReleasedListenerManager;
import app.tuxguitar.ui.qt.QTComponent;
import app.tuxguitar.ui.qt.resource.QTKey;
import org.qtjambi.qt.core.QEvent;
import org.qtjambi.qt.gui.QKeyEvent;

public class QTKeyReleasedListenerManager extends UIKeyReleasedListenerManager implements QTEventHandler {

	private QTComponent<?> control;

	public QTKeyReleasedListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public void handle(QKeyEvent event) {
		this.onKeyReleased(new UIKeyEvent(this.control, QTKey.getCombination(event)));
	}

	public boolean handle(QEvent event) {
		this.handle((QKeyEvent) event);

		return true;
	}
}