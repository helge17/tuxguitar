package org.herac.tuxguitar.ui.qt.event;

import org.herac.tuxguitar.ui.event.UIMouseEnterListenerManager;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.resource.UIPosition;
import io.qt.core.QEvent;
import io.qt.gui.QHoverEvent;

public class QTMouseEnterListenerManager extends UIMouseEnterListenerManager implements QTEventHandler {
	
	private QTComponent<?> control;
	
	public QTMouseEnterListenerManager(QTComponent<?> control) {
		this.control = control;
	}
	
	public void handle(QHoverEvent event) {
// TODO QT 5->6 //		this.onMouseEnter(new UIMouseEvent(this.control, new UIPosition(event.pos().x(), event.pos().y()), 0));
	}
	
	public boolean handle(QEvent event) {
		this.handle((QHoverEvent) event);
		
		return true;
	}
}
