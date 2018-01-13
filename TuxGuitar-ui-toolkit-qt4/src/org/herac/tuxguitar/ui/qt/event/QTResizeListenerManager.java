package org.herac.tuxguitar.ui.qt.event;

import org.herac.tuxguitar.ui.event.UIResizeEvent;
import org.herac.tuxguitar.ui.event.UIResizeListenerManager;
import org.herac.tuxguitar.ui.qt.QTComponent;

import com.trolltech.qt.core.QEvent;

public class QTResizeListenerManager extends UIResizeListenerManager implements QTEventHandler {
	
	private QTComponent<?> control;
	
	public QTResizeListenerManager(QTComponent<?> control) {
		this.control = control;
	}
	
	public boolean handle(QEvent event) {
		this.onResize(new UIResizeEvent(this.control));
		
		return true;
	}
}
