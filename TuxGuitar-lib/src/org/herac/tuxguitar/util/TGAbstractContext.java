package org.herac.tuxguitar.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TGAbstractContext {
	
	private Map<String, Object> attributes;
	
	public TGAbstractContext(){
		this.attributes = new ConcurrentHashMap<String, Object>();
	}
	
	public <T extends Object> void setAttribute(String key, T value){
		if( value != null ) {
			this.attributes.put(key, value);
		} else {
			this.removeAttribute(key);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T getAttribute(String key){
		if( this.hasAttribute(key) ) {
			return (T) this.attributes.get(key);
		}
		return null;
	}
	
	public void removeAttribute(String key){
		if( this.hasAttribute(key) ) {
			this.attributes.remove(key);
		}
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
	
	public void clear() {
		this.attributes.clear();
	}
	
	public void addContext(TGAbstractContext context) {
		this.addAttributes(context.getAttributes());
	}
}
