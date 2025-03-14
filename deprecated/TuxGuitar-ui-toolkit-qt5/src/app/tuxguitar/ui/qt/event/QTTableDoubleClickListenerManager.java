package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UIMouseDoubleClickListenerManager;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.qt.QTComponent;
import app.tuxguitar.ui.qt.resource.QTMouseButton;
import app.tuxguitar.ui.resource.UIPosition;
import org.qtjambi.qt.core.Qt.MouseButton;
import org.qtjambi.qt.widgets.QTableWidgetItem;

public class QTTableDoubleClickListenerManager extends UIMouseDoubleClickListenerManager {

	public static final String SIGNAL_METHOD = "handle(org.qtjambi.qt.widgets.QTableWidgetItem)";

	private QTComponent<?> control;

	public QTTableDoubleClickListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public void handle(QTableWidgetItem item) {
		this.onMouseDoubleClick(new UIMouseEvent(this.control, new UIPosition(), QTMouseButton.getMouseButton(MouseButton.LeftButton)));
	}
}
