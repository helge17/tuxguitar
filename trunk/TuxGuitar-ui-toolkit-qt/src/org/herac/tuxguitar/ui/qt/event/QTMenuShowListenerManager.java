package org.herac.tuxguitar.ui.qt.event;

import org.herac.tuxguitar.ui.event.UIMenuEvent;
import org.herac.tuxguitar.ui.event.UIMenuShowListenerManager;
import org.herac.tuxguitar.ui.qt.QTComponent;

import com.trolltech.qt.core.QEvent;

public class QTMenuShowListenerManager extends UIMenuShowListenerManager implements QTEventHandler, QTSignalHandler {
	
	private QTComponent<?> control;
	
	public QTMenuShowListenerManager(QTComponent<?> control) {
		this.control = control;
	}
	
	public void handle() {
		this.onMenuShow(new UIMenuEvent(this.control));
	}
	
	public void handle(QEvent event) {
		this.handle();
	}
}
