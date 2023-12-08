package org.herac.tuxguitar.midi.synth;

import java.util.HashMap;
import java.util.Map;

public class TGProgramElement {
	
	private String id;
	private String type;
	private Map<String, String> parameters;
	private boolean enabled;
	
	public TGProgramElement() {
		this.enabled = true;
		this.parameters = new HashMap<String, String>();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public Map<String, String> getParameters() {
		return this.parameters;
	}

	public void setParameter(String key, String value) {
		this.parameters.put(key, value);
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void copyFrom(TGProgramElement element) {
		this.setId(element.getId());
		this.setType(element.getType());
		this.setEnabled(element.isEnabled());
		this.getParameters().clear();
		this.getParameters().putAll(element.getParameters());
	}
	
	public int hashCode() {
		return (this.getId() != null ? this.getId().hashCode() : super.hashCode());
	}
	
	public boolean equals(Object obj) {
		if( obj instanceof TGProgramElement ) {
			return (this.hashCode() ==  obj.hashCode());
		}
		return super.equals(obj);
	}
}
