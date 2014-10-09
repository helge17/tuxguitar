package org.herac.tuxguitar.util;

import java.util.HashMap;
import java.util.Map;

public class TGContext {
	
	private Map attributes;
	
	public TGContext(){
		this.attributes = new HashMap();
	}
	
	public void setAttribute(String key, Object value){
		this.attributes.put(key, value);
	}
	
	public Object getAttribute(String key){
		return this.attributes.get(key);
	}
	
	public boolean hasAttribute(String key){
		return this.attributes.containsKey(key);
	}
}
