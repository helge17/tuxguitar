package org.herac.tuxguitar.app.system.keybindings;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.herac.tuxguitar.app.actions.Action;

public class KeyBindingActionManager {
	
	private KeyBindingListener listener;
	
	public KeyBindingActionManager(){
		this.init();
	}
	
	public void init(){
		this.listener = new KeyBindingListener();
	}
	
	public Action getActionForKeyBinding(KeyBinding kb){
		return KeyBindingActionList.getActionForKeyBinding(kb);
	}
	
	public KeyBinding getKeyBindingForAction(String action){
		return KeyBindingActionList.getKeyBindingForAction(action);
	}
	
	public void appendListenersTo(Component control){
		control.addKeyListener(this.listener);
	}
	
	protected class KeyBindingListener extends KeyAdapter {
		
		public void keyPressed(KeyEvent event) {
			KeyBinding kb = new KeyBinding();
			kb.setKey(event.getKeyCode());
			kb.setMask(event.getModifiersEx());
			Action action = getActionForKeyBinding(kb);
			if (action != null){
				action.process(event);
			}
		}
	}
}
