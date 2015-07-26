package org.herac.tuxguitar.app.system.icons;

import org.herac.tuxguitar.event.TGEvent;

public class TGIconEvent extends TGEvent {
	
	public static final String EVENT_TYPE = "ui-icon";
	
	public TGIconEvent() {
		super(EVENT_TYPE);
	}
}
