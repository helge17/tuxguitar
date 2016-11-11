package org.herac.tuxguitar.android.action;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionEvent;

public class TGActionAsyncProcessEndEvent extends TGActionEvent {
	
	public static final String EVENT_TYPE = "action-async-process-end";
	
	public TGActionAsyncProcessEndEvent(String actionId, TGActionContext actionContext) {
		super(EVENT_TYPE, actionId, actionContext);
	}
}
