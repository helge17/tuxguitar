package app.tuxguitar.app.system.icons;

import app.tuxguitar.event.TGEvent;

public class TGSkinEvent extends TGEvent {

	public static final String EVENT_TYPE = "ui-skin";

	public TGSkinEvent() {
		super(EVENT_TYPE);
	}
}
