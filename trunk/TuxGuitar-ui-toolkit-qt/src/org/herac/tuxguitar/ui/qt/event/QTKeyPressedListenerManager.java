package org.herac.tuxguitar.ui.qt.event;

import org.herac.tuxguitar.ui.event.UIKeyEvent;
import org.herac.tuxguitar.ui.event.UIKeyPressedListenerManager;
import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.qt.resource.QTKey;

import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.gui.QKeyEvent;

public class QTKeyPressedListenerManager extends UIKeyPressedListenerManager implements QTEventHandler {
	
	private QTComponent<?> control;
	
	public QTKeyPressedListenerManager(QTComponent<?> control) {
		this.control = control;
	}
	
	public void handle(QKeyEvent event) {
		this.onKeyPressed(new UIKeyEvent(this.control, QTKey.getConvination(event)));
	}
	
	public void handle(QEvent event) {
		this.handle((QKeyEvent) event);
	}
}
