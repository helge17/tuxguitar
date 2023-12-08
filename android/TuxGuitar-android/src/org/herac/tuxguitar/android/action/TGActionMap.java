package org.herac.tuxguitar.android.action;

import java.util.HashMap;
import java.util.Map;

public class TGActionMap<T> {
	
	private Map<String, T> map;
	
	public TGActionMap() {
		this.map = new HashMap<String, T>();
	}
	
	public void set(String actionId, T value) {
		if( value != null ) {
			this.map.put(actionId, value);
		} else if( this.map.containsKey(actionId) ) {
			this.map.remove(actionId);
		}
	}
	
	public T get(String actionId) {
		if( this.map.containsKey(actionId) ) {
			return this.map.get(actionId);
		}
		return null;
	}
}
