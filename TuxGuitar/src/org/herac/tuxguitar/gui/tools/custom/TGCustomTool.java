package org.herac.tuxguitar.gui.tools.custom;

public class TGCustomTool {
	
	private String name;
	private String action;
	
	public TGCustomTool(String name, String action) {
		super();
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
