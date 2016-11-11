package org.herac.tuxguitar.android.action;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionEvent;

public class TGActionAsyncProcessErrorEvent extends TGActionEvent {

	public static final String EVENT_TYPE = "action-async-process-error";

	public static final String PROPERTY_ACTION_ERROR = "actionError";
	
	public TGActionAsyncProcessErrorEvent(String actionId, TGActionContext actionContext, Throwable actionError) {
		super(EVENT_TYPE, actionId, actionContext);

		this.setAttribute(PROPERTY_ACTION_ERROR, actionError);
	}
}
