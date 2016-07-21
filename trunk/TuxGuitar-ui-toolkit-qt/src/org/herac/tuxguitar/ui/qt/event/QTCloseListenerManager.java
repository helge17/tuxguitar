package org.herac.tuxguitar.ui.qt.event;

import org.herac.tuxguitar.ui.event.UICloseEvent;
import org.herac.tuxguitar.ui.event.UICloseListenerManager;
import org.herac.tuxguitar.ui.qt.QTComponent;

import com.trolltech.qt.core.QEvent;

public class QTCloseListenerManager extends UICloseListenerManager implements QTEventHandler {
	
	private QTComponent<?> control;
	
	public QTCloseListenerManager(QTComponent<?> control) {
		this.control = control;
	}
	
	public QTComponent<?> getControl() {
		return this.control;
	}

	public void handle() {
		this.onClose(new UICloseEvent(this.control));
	}
	
	public void handle(QEvent event) {
		this.handle();
		
		event.ignore();
	}
}
