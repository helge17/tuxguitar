package org.herac.tuxguitar.action;

import java.util.HashMap;
import java.util.Map;

public abstract class TGActionContext {
	
	private Map<String, Object> attributes;
	
	public TGActionContext(){
		this.attributes = new HashMap<String, Object>();
	}
	
	public <T extends Object> void setAttribute(String key, T value){
		this.attributes.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T getAttribute(String key){
		return (T) this.attributes.get(key);
	}
	
	public Map<String, Object> getAttributes(){
		return this.attributes;
	}
}
