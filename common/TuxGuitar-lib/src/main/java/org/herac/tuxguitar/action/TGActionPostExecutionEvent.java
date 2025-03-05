package org.herac.tuxguitar.action;

public class TGActionPostExecutionEvent extends TGActionEvent {
	
	public static final String EVENT_TYPE = "action-post-execution";
	
	public TGActionPostExecutionEvent(String actionId, TGActionContext actionContext) {
		super(EVENT_TYPE, actionId, actionContext);
	}
}
