package org.herac.tuxguitar.action;

import org.herac.tuxguitar.event.TGEvent;

public abstract class TGActionEvent extends TGEvent {
	
	public static final String PROPERTY_ACTION_ID = "actionId";
	public static final String PROPERTY_ACTION_CONTEXT = "actionContext";
	
	public TGActionEvent(String eventType, String actionId, TGActionContext actionContext) {
		super(eventType);
		
		this.setProperty(PROPERTY_ACTION_ID, actionId);
		this.setProperty(PROPERTY_ACTION_CONTEXT, actionContext);
	}
}
