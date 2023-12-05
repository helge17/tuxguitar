package org.herac.tuxguitar.action;

public class TGActionPreExecutionEvent extends TGActionEvent {
	
	public static final String EVENT_TYPE = "action-pre-execution";
	
	public TGActionPreExecutionEvent(String actionId, TGActionContext actionContext) {
		super(EVENT_TYPE, actionId, actionContext);
	}
}
