package org.herac.tuxguitar.android.action;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionErrorEvent;

public class TGActionAsyncProcessErrorEvent extends TGActionErrorEvent {

	public static final String EVENT_TYPE = "action-async-process-error";

	public TGActionAsyncProcessErrorEvent(String actionId, TGActionContext actionContext, Throwable actionError) {
		super(EVENT_TYPE, actionId, actionContext, actionError);
	}
}
