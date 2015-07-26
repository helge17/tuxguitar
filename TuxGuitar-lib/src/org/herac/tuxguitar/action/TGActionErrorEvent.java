package org.herac.tuxguitar.action;

public class TGActionErrorEvent extends TGActionEvent {
	
	public static final String EVENT_TYPE = "action-error";

	public static final String PROPERTY_ACTION_ERROR = "actionError";
	
	public TGActionErrorEvent(String actionId, TGActionContext actionContext, Throwable actionError) {
		super(EVENT_TYPE, actionId, actionContext);

		this.setAttribute(PROPERTY_ACTION_ERROR, actionError);
	}
}
