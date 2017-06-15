package org.herac.tuxguitar.ui.qt.event;

import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListenerManager;
import org.herac.tuxguitar.ui.qt.QTComponent;

import com.trolltech.qt.core.QEvent;

public class QTSelectionListenerManager extends UISelectionListenerManager implements QTEventHandler, QTSignalHandler {
	
	private QTComponent<?> control;
	
	public QTSelectionListenerManager(QTComponent<?> control) {
		this.control = control;
	}
	
	public QTComponent<?> getControl() {
		return this.control;
	}
	
	public void handle() {
		this.onSelect(new UISelectionEvent(this.control));
	}
	
	public void handle(QEvent event) {
		this.handle();
	}
}
