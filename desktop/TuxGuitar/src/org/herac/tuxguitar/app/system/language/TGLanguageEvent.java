package org.herac.tuxguitar.app.system.language;

import org.herac.tuxguitar.event.TGEvent;

public class TGLanguageEvent extends TGEvent {
	
	public static final String EVENT_TYPE = "language";
	
	public TGLanguageEvent() {
		super(EVENT_TYPE);
	}
}
