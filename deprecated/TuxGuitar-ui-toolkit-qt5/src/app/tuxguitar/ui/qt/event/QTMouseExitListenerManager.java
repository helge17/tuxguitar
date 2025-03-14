package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.event.UIMouseExitListenerManager;
import app.tuxguitar.ui.qt.QTComponent;
import app.tuxguitar.ui.resource.UIPosition;
import org.qtjambi.qt.core.QEvent;
import org.qtjambi.qt.gui.QHoverEvent;

public class QTMouseExitListenerManager extends UIMouseExitListenerManager implements QTEventHandler {

	private QTComponent<?> control;

	public QTMouseExitListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public void handle(QHoverEvent event) {
		this.onMouseExit(new UIMouseEvent(this.control, new UIPosition(event.pos().x(), event.pos().y()), 0));
	}

	public boolean handle(QEvent event) {
		this.handle((QHoverEvent) event);

		return true;
	}
}
