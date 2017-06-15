package org.herac.tuxguitar.ui.qt.event;

import java.util.ArrayList;
import java.util.List;

import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.core.QEvent.Type;
import com.trolltech.qt.core.QObject;

public class QTEventFilter extends QObject {
	
	private boolean ignoreEvents;
	private List<QTEventMap> eventsMap;
	
	public QTEventFilter() {
		this.eventsMap = new ArrayList<QTEventMap>();
	}
	
	public void setIgnoreEvents(boolean ignoreEvents) {
		this.ignoreEvents = ignoreEvents;
	}

	public void connect(Type eventType, QTEventHandler eventHandler) {
		this.eventsMap.add(new QTEventMap(eventType, eventHandler));
	}
	
	public void disconnect(Type eventType, QTEventHandler eventHandler) {
		QTEventMap eventMap = this.findEventMap(eventType, eventHandler);
		if( eventMap != null ) {
			this.eventsMap.remove(eventMap);
		}
	}
	
	public QTEventMap findEventMap(Type eventType, QTEventHandler eventHandler) {
		for(QTEventMap map : this.eventsMap) {
			if( map.getEventType().equals(eventType) && map.getEventHandler().equals(eventHandler) ) {
				return map;
			}
		}
		return null;
	}
	
	public boolean eventFilter(QObject obj, QEvent event) {
		boolean success = false;
		if(!this.ignoreEvents) {
			for(QTEventMap map : this.eventsMap) {
				if( map.getEventType().equals(event.type()) ) {
					map.getEventHandler().handle(event);
					success = true;
				}
			}
		}
		return (success || super.eventFilter(obj, event));
	}
}
