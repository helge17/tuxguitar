package org.herac.tuxguitar.app.editors;

import org.herac.tuxguitar.event.TGEvent;

public class TGUpdateEvent extends TGEvent {
	
	public static final String EVENT_TYPE = "ui-update";
	public static final String PROPERTY_UPDATE_MODE = "updateMode";
	
	public static final int SELECTION = 1;
	public static final int MEASURE_UPDATED = 2;
	public static final int SONG_UPDATED = 3;
	public static final int SONG_LOADED = 4;
	public static final int SONG_SAVED = 5;
	
	public TGUpdateEvent(int updateMode) {
		super(EVENT_TYPE);
		
		this.setProperty(PROPERTY_UPDATE_MODE, Integer.valueOf(updateMode));
	}
}
