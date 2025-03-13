package app.tuxguitar.app.system.language;

import app.tuxguitar.event.TGEvent;

public class TGLanguageEvent extends TGEvent {

	public static final String EVENT_TYPE = "language";

	public TGLanguageEvent() {
		super(EVENT_TYPE);
	}
}
