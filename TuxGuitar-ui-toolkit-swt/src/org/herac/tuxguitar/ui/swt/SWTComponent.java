package org.herac.tuxguitar.ui.swt;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.ui.UIComponent;

public abstract class SWTComponent<T> implements UIComponent {
	
	private T component;
	private Map<String, Object> data;
	
	public SWTComponent(T component) {
		this.component = component;
	}
	
	public T getControl() {
		return this.component;
	}
	
	@SuppressWarnings("unchecked")
	public <Data> Data getData(String key) {
		return (Data) this.getData().get(key);
	}
	
	public <Data> void setData(String key, Data data) {
		this.getData().put(key, data);
	}
	
	public Map<String, Object> getData() {
		if( this.data == null ) {
			this.data = new HashMap<String, Object>();
		}
		return this.data;
	}
}