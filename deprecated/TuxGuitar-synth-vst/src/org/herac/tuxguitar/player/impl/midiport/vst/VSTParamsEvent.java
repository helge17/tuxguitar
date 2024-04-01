package org.herac.tuxguitar.player.impl.midiport.vst;

import java.util.Map;

import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.midi.synth.remote.TGSession;

public class VSTParamsEvent extends TGEvent {
	
	public static final String EVENT_TYPE = "vst-params";
	public static final String PROPERTY_ACTION = "action";
	public static final String PROPERTY_SESSION = "session";
	public static final String PROPERTY_PARAMS = "parameters";
	
	public static final Integer ACTION_STORE = 1;
	public static final Integer ACTION_RESTORE = 2;
	
	public VSTParamsEvent(TGSession session, Integer action, Map<String, String> parameters) {
		super(EVENT_TYPE);
		
		this.setAttribute(PROPERTY_SESSION, session);
		this.setAttribute(PROPERTY_ACTION, action);
		this.setAttribute(PROPERTY_PARAMS, parameters);
	}
}
