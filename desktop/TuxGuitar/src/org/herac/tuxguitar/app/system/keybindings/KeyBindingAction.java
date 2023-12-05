package org.herac.tuxguitar.app.system.keybindings;

import org.herac.tuxguitar.ui.resource.UIKeyCombination;

public class KeyBindingAction {
	
	private String action;
	private UIKeyCombination combination;
	
	public KeyBindingAction(String action, UIKeyCombination combination){
		this.action = action;
		this.combination = combination;
	}
	
	public String getAction() {
		return this.action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public UIKeyCombination getCombination() {
		return this.combination;
	}
	
	public void setCombination(UIKeyCombination combination) {
		this.combination = combination;
	}
}
