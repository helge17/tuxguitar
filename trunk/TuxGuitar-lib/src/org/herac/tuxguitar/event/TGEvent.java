package org.herac.tuxguitar.event;

import java.util.HashMap;
import java.util.Map;

public class TGEvent {

	private String eventType;
	private Map<String, Object> properties;
	
	public TGEvent(String eventType) {
		this.eventType = eventType;
		this.properties = new HashMap<String, Object>();
	}
	
	public String getEventType() {
		return eventType;
	}
	
	public <T extends Object> void setProperty(String key, T value){
		this.properties.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T getProperty(String key){
		return (T) this.properties.get(key);
	}
}
