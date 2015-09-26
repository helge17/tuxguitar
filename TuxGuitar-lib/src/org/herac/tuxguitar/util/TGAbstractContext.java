package org.herac.tuxguitar.util;

import java.util.HashMap;
import java.util.Map;

public abstract class TGAbstractContext {
	
	private Map<String, Object> attributes;
	
	public TGAbstractContext(){
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
	
	public void addAttributes(Map<String, Object> attributes) {
		this.attributes.putAll(attributes);
	}
	
	public boolean hasAttribute(String key){
		return this.attributes.containsKey(key);
	}
	
	public void removeAttribute(String key){
		this.attributes.remove(key);
	}
	
	public void clear() {
		this.attributes.clear();
	}
	
	public void addContext(TGAbstractContext context) {
		this.addAttributes(context.getAttributes());
	}
}
