package app.tuxguitar.editor.event;

import app.tuxguitar.event.TGEvent;
import app.tuxguitar.util.TGAbstractContext;

public class TGDestroyEvent extends TGEvent {

	public static final String EVENT_TYPE = "ui-destroy";

	public TGDestroyEvent(TGAbstractContext sourceContext) {
		super(EVENT_TYPE, sourceContext);
	}
}
