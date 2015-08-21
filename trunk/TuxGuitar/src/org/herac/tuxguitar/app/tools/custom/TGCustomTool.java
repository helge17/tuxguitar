package org.herac.tuxguitar.app.tools.custom;

public class TGCustomTool {
	
	private String name;
	private String action;
	
	public TGCustomTool(String name, String action) {
		this.name = name;
		this.action = action;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getAction() {
		return this.action;
	}
}
