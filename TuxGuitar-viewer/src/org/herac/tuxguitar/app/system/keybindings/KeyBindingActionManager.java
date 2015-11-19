package org.herac.tuxguitar.app.system.keybindings;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;

public class KeyBindingActionManager {
	
	private TGContext context;
	private KeyBindingListener listener;
	
	public KeyBindingActionManager(TGContext context){
		this.context = context;
		this.init();
	}
	
	public void init(){
		this.listener = new KeyBindingListener();
	}
	
	public String getActionForKeyBinding(KeyBinding kb){
		return KeyBindingActionList.getActionForKeyBinding(kb);
	}
	
	public KeyBinding getKeyBindingForAction(String action){
		return KeyBindingActionList.getKeyBindingForAction(action);
	}
	
	public void appendListenersTo(Component control){
		control.addKeyListener(this.listener);
	}
	
	public void processKeyBinding(KeyBinding kb){
		final String actionId = getActionForKeyBinding(kb);
		if( actionId != null ){
			new TGActionProcessor(this.context, actionId).process();
		}
	}
	
	protected class KeyBindingListener extends KeyAdapter {
		
		public void keyPressed(KeyEvent event) {
			KeyBinding kb = new KeyBinding();
			kb.setKey(event.getKeyCode());
			kb.setMask(event.getModifiersEx());
			processKeyBinding(kb);
		}
	}
}
