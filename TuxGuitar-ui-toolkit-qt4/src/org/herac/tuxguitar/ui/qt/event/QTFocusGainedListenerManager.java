package org.herac.tuxguitar.ui.qt.event;

import org.herac.tuxguitar.ui.event.UIFocusEvent;
import org.herac.tuxguitar.ui.event.UIFocusGainedListenerManager;
import org.herac.tuxguitar.ui.qt.QTComponent;

import com.trolltech.qt.core.QEvent;

public class QTFocusGainedListenerManager extends UIFocusGainedListenerManager implements QTEventHandler {
	
	private QTComponent<?> control;
	
	public QTFocusGainedListenerManager(QTComponent<?> control) {
		this.control = control;
	}
	
	public void handle(QEvent event) {
		this.onFocusGained(new UIFocusEvent(this.control));
	}
}
