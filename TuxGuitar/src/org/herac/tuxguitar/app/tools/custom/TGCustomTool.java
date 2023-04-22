package org.herac.tuxguitar.app.tools.custom;

public class TGCustomTool {
	
	private String name;
	private String label;
	private String action;
	
	public TGCustomTool(String name, String action, String label) {
		this.name = name;
		this.action = action;
		this.label = label;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getAction() {
		return this.action;
	}
	
	public String getLabel() {
		return this.label;
	}

}
