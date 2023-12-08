package org.herac.tuxguitar.editor.event;

import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.util.TGAbstractContext;

public class TGDestroyEvent extends TGEvent {
	
	public static final String EVENT_TYPE = "ui-destroy";
	
	public TGDestroyEvent(TGAbstractContext sourceContext) {
		super(EVENT_TYPE, sourceContext);
	}
}
