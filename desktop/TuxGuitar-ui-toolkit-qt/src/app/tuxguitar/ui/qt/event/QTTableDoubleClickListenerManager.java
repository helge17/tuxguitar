package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UIMouseDoubleClickListenerManager;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.qt.QTComponent;
import app.tuxguitar.ui.qt.resource.QTMouseButton;
import app.tuxguitar.ui.resource.UIPosition;
import io.qt.core.Qt.MouseButton;
import io.qt.widgets.QTableWidgetItem;

public class QTTableDoubleClickListenerManager extends UIMouseDoubleClickListenerManager {

	public static final String SIGNAL_METHOD = "handle(io.qt.widgets.QTableWidgetItem)";

	private QTComponent<?> control;

	public QTTableDoubleClickListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public void handle(QTableWidgetItem item) {
// TODO QT 5->6 //		this.onMouseDoubleClick(new UIMouseEvent(this.control, new UIPosition(), QTMouseButton.getMouseButton(MouseButton.LeftButton)));
	}
}
