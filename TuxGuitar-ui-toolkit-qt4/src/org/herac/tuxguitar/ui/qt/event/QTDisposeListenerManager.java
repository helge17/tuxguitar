package org.herac.tuxguitar.ui.qt.event;

import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListenerManager;
import org.herac.tuxguitar.ui.qt.QTComponent;

import com.trolltech.qt.core.QEvent;

public class QTDisposeListenerManager extends UIDisposeListenerManager implements QTEventHandler {
	
	private QTComponent<?> control;
	
	public QTDisposeListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public void handle(QEvent event) {
		this.onDispose(new UIDisposeEvent(this.control));
	}
}
