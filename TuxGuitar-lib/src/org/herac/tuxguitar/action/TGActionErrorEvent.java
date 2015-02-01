package org.herac.tuxguitar.action;

import org.herac.tuxguitar.event.TGEvent;

public class TGActionErrorEvent extends TGEvent {
	
	public static final String EVENT_TYPE = "action-error";
	
	public static final String PROPERTY_ACTION_ID = "actionId";
	public static final String PROPERTY_ACTION_CONTEXT = "actionContext";
	public static final String PROPERTY_ACTION_ERROR = "actionError";
	
	public TGActionErrorEvent(String actionId, TGActionContext actionContext, Throwable actionError) {
		super(EVENT_TYPE);
		
		this.setProperty(PROPERTY_ACTION_ID, actionId);
		this.setProperty(PROPERTY_ACTION_CONTEXT, actionContext);
		this.setProperty(PROPERTY_ACTION_ERROR, actionError);
	}
}
