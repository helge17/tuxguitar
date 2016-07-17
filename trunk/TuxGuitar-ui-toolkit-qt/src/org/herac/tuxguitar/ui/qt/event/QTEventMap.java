package org.herac.tuxguitar.ui.qt.event;

import com.trolltech.qt.core.QEvent.Type;

public class QTEventMap {
	
	private Type eventType;
	private QTEventHandler eventHandler;
	
	public QTEventMap(Type eventType, QTEventHandler eventHandler) {
		this.eventType = eventType;
		this.eventHandler = eventHandler;
	}

	public Type getEventType() {
		return eventType;
	}

	public QTEventHandler getEventHandler() {
		return eventHandler;
	}
}
