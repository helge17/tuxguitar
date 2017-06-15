package org.herac.tuxguitar.ui.qt.event;

import org.herac.tuxguitar.ui.event.UIMouseDownListenerManager;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.qt.resource.QTMouseButton;
import org.herac.tuxguitar.ui.resource.UIPosition;

import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.gui.QMouseEvent;

public class QTMouseDownListenerManager extends UIMouseDownListenerManager implements QTEventHandler {
	
	private QTComponent<?> control;
	
	public QTMouseDownListenerManager(QTComponent<?> control) {
		this.control = control;
	}
	
	public void handle(QMouseEvent event) {
		this.onMouseDown(new UIMouseEvent(this.control, new UIPosition(event.x(), event.y()), QTMouseButton.getMouseButton(event.button())));
	}
	
	public void handle(QEvent event) {
		this.handle((QMouseEvent) event);
	}
}
