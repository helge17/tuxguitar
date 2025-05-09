package app.tuxguitar.action;

import app.tuxguitar.event.TGEvent;

public abstract class TGActionEvent extends TGEvent {

	public static final String ATTRIBUTE_ACTION_ID = "actionId";

	public TGActionEvent(String eventType, String actionId, TGActionContext actionContext) {
		super(eventType, actionContext);

		this.setAttribute(ATTRIBUTE_ACTION_ID, actionId);
	}
}
