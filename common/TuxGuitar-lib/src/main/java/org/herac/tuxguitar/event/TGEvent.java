package org.herac.tuxguitar.event;

import org.herac.tuxguitar.util.TGAbstractContext;

public class TGEvent extends TGAbstractContext {
	
	public static final String ATTRIBUTE_SOURCE_CONTEXT = "sourceContext";
	
	private String eventType;
	
	public TGEvent(String eventType, TGAbstractContext sourceContext) {
		this.eventType = eventType;
		this.setAttribute(ATTRIBUTE_SOURCE_CONTEXT, sourceContext);
	}
	
	public TGEvent(String eventType) {
		this(eventType,  null);
	}
	
	public String getEventType() {
		return eventType;
	}
}
