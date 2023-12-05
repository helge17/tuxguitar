package org.herac.tuxguitar.player.impl.midiport.lv2;

import java.util.Map;

import org.herac.tuxguitar.event.TGEvent;

public class LV2ParamsEvent extends TGEvent {
	
	public static final String EVENT_TYPE = "lv2-params";
	public static final String PROPERTY_PROCESSOR = "processor";
	public static final String PROPERTY_ACTION = "action";
	public static final String PROPERTY_PARAMS = "parameters";
	
	public static final Integer ACTION_STORE = 1;
	public static final Integer ACTION_RESTORE = 2;
	
	public LV2ParamsEvent(LV2AudioProcessor processor, Integer action, Map<String, String> parameters) {
		super(EVENT_TYPE);
		
		this.setAttribute(PROPERTY_PROCESSOR, processor);
		this.setAttribute(PROPERTY_ACTION, action);
		this.setAttribute(PROPERTY_PARAMS, parameters);
	}
}
