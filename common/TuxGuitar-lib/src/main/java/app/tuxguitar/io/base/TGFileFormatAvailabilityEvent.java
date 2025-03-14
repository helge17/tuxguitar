package app.tuxguitar.io.base;

import app.tuxguitar.event.TGEvent;

public class TGFileFormatAvailabilityEvent extends TGEvent {

	public static final String EVENT_TYPE = "file-format-availability";

	public TGFileFormatAvailabilityEvent() {
		super(EVENT_TYPE);
	}
}
