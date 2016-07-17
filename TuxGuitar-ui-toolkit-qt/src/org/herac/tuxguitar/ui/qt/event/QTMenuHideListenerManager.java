package org.herac.tuxguitar.ui.qt.event;

import org.herac.tuxguitar.ui.event.UIMenuEvent;
import org.herac.tuxguitar.ui.event.UIMenuHideListenerManager;
import org.herac.tuxguitar.ui.qt.QTComponent;

import com.trolltech.qt.core.QEvent;

public class QTMenuHideListenerManager extends UIMenuHideListenerManager implements QTEventHandler, QTSignalHandler {
	
	private QTComponent<?> control;
	
	public QTMenuHideListenerManager(QTComponent<?> control) {
		this.control = control;
	}
	
	public void handle() {
		this.onMenuHide(new UIMenuEvent(this.control));
	}
	
	public void handle(QEvent event) {
		this.handle();
	}
}
