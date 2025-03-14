package app.tuxguitar.app.tools.scale;

import app.tuxguitar.event.TGEvent;

public class ScaleEvent extends TGEvent {

	public static final String EVENT_TYPE = "ui-scale-changed";

	public ScaleEvent() {
		super(EVENT_TYPE);
	}
}
