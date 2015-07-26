package org.herac.tuxguitar.app.tools.scale;

import org.herac.tuxguitar.event.TGEvent;

public class ScaleEvent extends TGEvent {
	
	public static final String EVENT_TYPE = "ui-scale-changed";
	
	public ScaleEvent() {
		super(EVENT_TYPE);
	}
}
