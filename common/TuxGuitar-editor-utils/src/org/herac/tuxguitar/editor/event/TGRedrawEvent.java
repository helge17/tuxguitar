package org.herac.tuxguitar.editor.event;

import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.util.TGAbstractContext;

public class TGRedrawEvent extends TGEvent {
	
	public static final String EVENT_TYPE = "ui-redraw";
	public static final String PROPERTY_REDRAW_MODE = "redrawMode";
	
	public static final int NORMAL = 1;
	public static final int PLAYING_THREAD = 2;
	public static final int PLAYING_NEW_BEAT = 3;
	
	public TGRedrawEvent(int redrawMode, TGAbstractContext sourceContext) {
		super(EVENT_TYPE, sourceContext);
		
		this.setAttribute(PROPERTY_REDRAW_MODE, Integer.valueOf(redrawMode));
	}
}
