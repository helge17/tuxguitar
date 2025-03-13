package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UIMouseWheelEvent;
import app.tuxguitar.ui.event.UIMouseWheelListenerManager;
import app.tuxguitar.ui.qt.QTComponent;
import app.tuxguitar.ui.resource.UIPosition;
import io.qt.core.QEvent;
import io.qt.gui.QWheelEvent;

public class QTMouseWheelListenerManager extends UIMouseWheelListenerManager implements QTEventHandler {

	private QTComponent<?> control;

	public QTMouseWheelListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public void handle(QWheelEvent event) {
// TODO QT 5->6 //		this.onMouseWheel(new UIMouseWheelEvent(this.control, new UIPosition(event.x(), event.y()), 2, event.delta()));
	}

	public boolean handle(QEvent event) {
		this.handle((QWheelEvent) event);

		return true;
	}
}
