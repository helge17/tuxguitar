package org.herac.tuxguitar.app.system.keybindings;

import org.herac.tuxguitar.ui.resource.UIKeyConvination;

public class KeyBindingAction {
	
	private String action;
	private UIKeyConvination convination;
	
	public KeyBindingAction(String action, UIKeyConvination convination){
		this.action = action;
		this.convination = convination;
	}
	
	public String getAction() {
		return this.action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public UIKeyConvination getConvination() {
		return this.convination;
	}
	
	public void setConvination(UIKeyConvination convination) {
		this.convination = convination;
	}
}
