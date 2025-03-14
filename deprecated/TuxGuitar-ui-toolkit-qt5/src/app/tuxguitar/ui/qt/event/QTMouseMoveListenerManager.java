package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.event.UIMouseMoveListenerManager;
import app.tuxguitar.ui.qt.QTComponent;
import app.tuxguitar.ui.qt.resource.QTMouseButton;
import app.tuxguitar.ui.resource.UIPosition;
import org.qtjambi.qt.core.QEvent;
import org.qtjambi.qt.gui.QMouseEvent;

public class QTMouseMoveListenerManager extends UIMouseMoveListenerManager implements QTEventHandler {

	private QTComponent<?> control;

	public QTMouseMoveListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public void handle(QMouseEvent event) {
		this.onMouseMove(new UIMouseEvent(this.control, new UIPosition(event.x(), event.y()), QTMouseButton.getMouseButton(event.button())));
	}

	public boolean handle(QEvent event) {
		this.handle((QMouseEvent) event);

		return true;
	}
}
