package org.herac.tuxguitar.util;

import java.util.HashMap;
import java.util.Map;

public class TGContext {
	
	private Map<String, Object> attributes;
	
	public TGContext(){
		this.attributes = new HashMap<String, Object>();
	}
	
	public <T extends Object> void setAttribute(String key, T value){
		this.attributes.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T getAttribute(String key){
		return (T) this.attributes.get(key);
	}
	
	public boolean hasAttribute(String key){
		return this.attributes.containsKey(key);
	}
	
	public void clear() {
		this.attributes.clear();
	}
}
