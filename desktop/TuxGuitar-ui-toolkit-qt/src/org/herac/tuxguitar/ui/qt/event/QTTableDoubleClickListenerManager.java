package org.herac.tuxguitar.ui.qt.event;

import org.herac.tuxguitar.ui.event.UIMouseDoubleClickListenerManager;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.qt.resource.QTMouseButton;
import org.herac.tuxguitar.ui.resource.UIPosition;
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
