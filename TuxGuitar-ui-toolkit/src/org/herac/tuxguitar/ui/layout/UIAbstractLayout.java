package org.herac.tuxguitar.ui.layout;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.ui.widget.UIControl;

public abstract class UIAbstractLayout implements UILayout {
	
	private UILayoutAttributes attributes;
	private Map<UIControl, UILayoutAttributes> controlAttributes;
	
	public UIAbstractLayout() {
		this.attributes = new UILayoutAttributes();
		this.controlAttributes = new HashMap<UIControl, UILayoutAttributes>();
	}

	public UILayoutAttributes getAttributes() {
		return attributes;
	}

	public UILayoutAttributes getControlAttributes(UIControl control) {
		if( this.controlAttributes.containsKey(control) ) {
			return this.controlAttributes.get(control);
		}
		this.controlAttributes.put(control, new UILayoutAttributes());
		
		return this.getControlAttributes(control);
	}
	
	public <T extends Object> void set(String key, T value){
		this.getAttributes().set(key, value);
	}
	
	public <T extends Object> void set(UIControl control, String key, T value){
		this.getControlAttributes(control).set(key, value);
	}
	
	public <T extends Object> T get(String key){
		return this.getAttributes().get(key);
	}
	
	public <T extends Object> T get(UIControl control, String key){
		return this.getControlAttributes(control).get(key);
	}
	
	public <T extends Object> T get(String key, T defaultValue){
		T value = this.get(key);
		return (value != null ? value : defaultValue);
	}
	
	public <T extends Object> T get(UIControl control, String key, T defaultValue){
		T value = this.get(control, key);
		return (value != null ? value : defaultValue);
	}
}
