package org.herac.tuxguitar.event;

import java.util.HashMap;
import java.util.Map;

public class TGEvent {

	private String eventType;
	private Map properties;
	
	public TGEvent(String eventType) {
		this.eventType = eventType;
		this.properties = new HashMap();
	}
	
	public String getEventType() {
		return eventType;
	}
	
	public void setProperty(String key, Object value){
		this.properties.put(key, value);
	}
	
	public Object getProperty(String key){
		return this.properties.get(key);
	}
}
