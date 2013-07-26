package org.herac.tuxguitar.action;

import java.util.HashMap;
import java.util.Map;

public abstract class TGActionContext {
	
	private Map attributes;
	
	public TGActionContext(){
		this.attributes = new HashMap();
	}
	
	public void setAttribute(String key, Object value){
		this.attributes.put(key, value);
	}
	
	public Object getAttribute(String key){
		return this.attributes.get(key);
	}
}
