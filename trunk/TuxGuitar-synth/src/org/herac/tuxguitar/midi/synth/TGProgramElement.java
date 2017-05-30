package org.herac.tuxguitar.midi.synth;

import java.util.HashMap;
import java.util.Map;

public class TGProgramElement {
	
	private String id;
	private String type;
	private Map<String, String> parameters;
	
	public TGProgramElement() {
		this.parameters = new HashMap<String, String>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameter(String key, String value) {
		this.parameters.put(key, value);
	}
	
	public void copyFrom(TGProgramElement element) {
		this.setId(element.getId());
		this.setType(element.getType());
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
