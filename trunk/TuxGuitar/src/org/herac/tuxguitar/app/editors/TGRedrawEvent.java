package org.herac.tuxguitar.app.editors;

import org.herac.tuxguitar.event.TGEvent;

public class TGRedrawEvent extends TGEvent {
	
	public static final String EVENT_TYPE = "ui-redraw";
	public static final String PROPERTY_REDRAW_MODE = "redrawMode";
	
	public static final int NORMAL = 1;
	public static final int PLAYING_THREAD = 2;
	public static final int PLAYING_NEW_BEAT = 3;
	
	public TGRedrawEvent(int redrawMode) {
		super(EVENT_TYPE);
		
		this.setProperty(PROPERTY_REDRAW_MODE, Integer.valueOf(redrawMode));
	}
}
