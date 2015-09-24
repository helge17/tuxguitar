package org.herac.tuxguitar.android.view.dialog;

import java.util.HashMap;
import java.util.Map;

public class TGDialogContext {

	private Map<String, Object> attributes;
	
	public TGDialogContext(){
		this.attributes = new HashMap<String, Object>();
	}
	
	public void setAttribute(String key, Object value){
		this.attributes.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key){
		return (T)this.attributes.get(key);
	}
}
