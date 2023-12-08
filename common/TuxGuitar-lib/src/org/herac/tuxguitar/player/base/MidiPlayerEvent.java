package org.herac.tuxguitar.player.base;

import org.herac.tuxguitar.event.TGEvent;

public class MidiPlayerEvent extends TGEvent {
	
	public static final String EVENT_TYPE = "midi-player-notification";
	
	public static final String PROPERTY_NOTIFICATION_TYPE = "notificationType";
	
	public static final int NOTIFY_STARTED = 1;
	public static final int NOTIFY_STOPPED = 2;
	public static final int NOTIFY_COUNT_DOWN_STARTED = 3;
	public static final int NOTIFY_COUNT_DOWN_STOPPED = 4;
	public static final int NOTIFY_LOOP = 5;
	
	public MidiPlayerEvent(int notificationType) {
		super(EVENT_TYPE);
		
		this.setAttribute(PROPERTY_NOTIFICATION_TYPE, Integer.valueOf(notificationType));
	}
}
