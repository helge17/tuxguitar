package org.herac.tuxguitar.app.editor;

import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.util.TGAbstractContext;

public class TGExternalBeatViewerEvent extends TGEvent {
	
	public static final String EVENT_TYPE = "ui-external-beat-viewer";
	public static final String PROPERTY_ACTION = "action";
	public static final String PROPERTY_BEAT = "beat";
	
	public static final String ACTION_SHOW = "show";
	public static final String ACTION_HIDE = "hide";
	
	public TGExternalBeatViewerEvent(String action, TGAbstractContext context) {
		super(EVENT_TYPE, context);
		
		this.setAttribute(PROPERTY_ACTION, action);
	}
	
	public TGExternalBeatViewerEvent(String action, TGBeat beat, TGAbstractContext context) {
		this(action, context);
		
		this.setAttribute(PROPERTY_BEAT, beat);
	}
}
