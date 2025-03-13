package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UIZoomEvent;
import app.tuxguitar.ui.event.UIZoomListenerManager;
import app.tuxguitar.ui.qt.QTComponent;
import io.qt.core.QEvent;
import io.qt.core.Qt.KeyboardModifier;
import io.qt.gui.QWheelEvent;

public class QTZoomListenerManager extends UIZoomListenerManager implements QTEventHandler {

	private QTComponent<?> control;

	public QTZoomListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public boolean handle(QWheelEvent event) {
		if( event.modifiers().isSet(KeyboardModifier.ControlModifier) ) {
// TODO QT 5->6 //			this.onZoom(new UIZoomEvent(this.control, (event.delta() > 0 ? 1 : -1)));

			return true;
		}
		return false;
	}

	public boolean handle(QEvent event) {
		return this.handle((QWheelEvent) event);
	}
}
