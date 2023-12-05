package org.herac.tuxguitar.io.base;

import org.herac.tuxguitar.event.TGEvent;

public class TGFileFormatAvailabilityEvent extends TGEvent {
	
	public static final String EVENT_TYPE = "file-format-availability";
	
	public TGFileFormatAvailabilityEvent() {
		super(EVENT_TYPE);
	}
}
