package org.herac.tuxguitar.ui.qt.event;

import org.herac.tuxguitar.ui.event.UIKeyEvent;
import org.herac.tuxguitar.ui.event.UIKeyReleasedListenerManager;
import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.qt.resource.QTKey;

import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.gui.QKeyEvent;

public class QTKeyReleasedListenerManager extends UIKeyReleasedListenerManager implements QTEventHandler {
	
	private QTComponent<?> control;
	
	public QTKeyReleasedListenerManager(QTComponent<?> control) {
		this.control = control;
	}
	
	public void handle(QKeyEvent event) {
		this.onKeyReleased(new UIKeyEvent(this.control, QTKey.getConvination(event)));
	}
	
	public void handle(QEvent event) {
		this.handle((QKeyEvent) event);
	}
}