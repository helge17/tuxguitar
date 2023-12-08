package org.herac.tuxguitar.ui.layout;

import java.util.HashMap;
import java.util.Map;

public class UILayoutAttributes {
	
	private Map<String, Object> attributes;
	
	public UILayoutAttributes(){
		this.attributes = new HashMap<String, Object>();
	}
	
	public <T extends Object> void set(String key, T value){
		if( value != null ) {
			this.attributes.put(key, value);
		} else {
			this.remove(key);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T get(String key){
		if( this.contains(key) ) {
			return (T) this.attributes.get(key);
		}
		return null;
	}
	
	public void remove(String key){
		if( this.contains(key) ) {
			this.attributes.remove(key);
		}
	}
	
	public boolean contains(String key){
		return this.attributes.containsKey(key);
	}
	
	public void clear() {
		this.attributes.clear();
	}
}
