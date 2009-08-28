package org.herac.tuxguitar.gui.system.keybindings;

public class KeyBindingAction {
	
	private String action;
	private KeyBinding keyBinding;
	
	public KeyBindingAction(String action,KeyBinding keyBinding){
		this.action = action;
		this.keyBinding = keyBinding;
	}
	
	public String getAction() {
		return this.action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public KeyBinding getKeyBinding() {
		return this.keyBinding;
	}
	
	public void setKeyBinding(KeyBinding keyBinding) {
		this.keyBinding = keyBinding;
	}
}
