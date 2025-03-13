package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UIMouseDoubleClickListenerManager;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.qt.QTComponent;
import app.tuxguitar.ui.qt.resource.QTMouseButton;
import app.tuxguitar.ui.resource.UIPosition;
import io.qt.core.QEvent;
import io.qt.gui.QMouseEvent;

public class QTMouseDoubleClickListenerManager extends UIMouseDoubleClickListenerManager implements QTEventHandler {

	private QTComponent<?> control;

	public QTMouseDoubleClickListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public void handle(QMouseEvent event) {
// TODO QT 5->6 //		this.onMouseDoubleClick(new UIMouseEvent(this.control, new UIPosition(event.x(), event.y()), QTMouseButton.getMouseButton(event.button())));
	}

	public boolean handle(QEvent event) {
		this.handle((QMouseEvent) event);

		return true;
	}
}
