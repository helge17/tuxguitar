package app.tuxguitar.editor.event;

import app.tuxguitar.event.TGEvent;
import app.tuxguitar.util.TGAbstractContext;

public class TGUpdateEvent extends TGEvent {

	public static final String EVENT_TYPE = "ui-update";
	public static final String PROPERTY_UPDATE_MODE = "updateMode";

	public static final int SELECTION = 1;
	public static final int MEASURE_UPDATED = 2;
	public static final int SONG_UPDATED = 3;
	public static final int SONG_LOADED = 4;
	public static final int SONG_SAVED = 5;
	public static final int CUSTOM_TEMPLATE_CHANGED = 6;
	public static final int VOLUME_CHANGED = 7;

	public TGUpdateEvent(int updateMode, TGAbstractContext context) {
		super(EVENT_TYPE, context);

		this.setAttribute(PROPERTY_UPDATE_MODE, Integer.valueOf(updateMode));
	}
}
