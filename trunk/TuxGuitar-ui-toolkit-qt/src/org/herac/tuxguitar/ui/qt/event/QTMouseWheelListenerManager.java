package org.herac.tuxguitar.ui.qt.event;

import org.herac.tuxguitar.ui.event.UIMouseWheelEvent;
import org.herac.tuxguitar.ui.event.UIMouseWheelListenerManager;
import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.resource.UIPosition;

import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.gui.QWheelEvent;

public class QTMouseWheelListenerManager extends UIMouseWheelListenerManager implements QTEventHandler {
	
	private QTComponent<?> control;
	
	public QTMouseWheelListenerManager(QTComponent<?> control) {
		this.control = control;
	}
	
	public void handle(QWheelEvent event) {
		this.onMouseWheel(new UIMouseWheelEvent(this.control, new UIPosition(event.x(), event.y()), 2, event.delta()));
	}
	
	public void handle(QEvent event) {
		this.handle((QWheelEvent) event);
	}
}
