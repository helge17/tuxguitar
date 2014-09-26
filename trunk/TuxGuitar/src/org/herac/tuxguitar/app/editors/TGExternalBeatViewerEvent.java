package org.herac.tuxguitar.app.editors;

import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.song.models.TGBeat;

public class TGExternalBeatViewerEvent extends TGEvent {
	
	public static final String EVENT_TYPE = "ui-external-beat-viewer";
	public static final String PROPERTY_ACTION = "action";
	public static final String PROPERTY_BEAT = "beat";
	
	public static final String ACTION_SHOW = "show";
	public static final String ACTION_HIDE = "hide";
	
	public TGExternalBeatViewerEvent(String action) {
		super(EVENT_TYPE);
		
		this.setProperty(ACTION_SHOW, action);
	}
	
	public TGExternalBeatViewerEvent(String action, TGBeat beat) {
		this(action);
		
		this.setProperty(PROPERTY_BEAT, beat);
	}
}
