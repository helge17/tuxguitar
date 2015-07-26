package org.herac.tuxguitar.app.system.keybindings;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

public class KeyBindingListener implements KeyListener {
	
	private KeyBindingActionManager keyBindingActionManager;
	
	public KeyBindingListener(KeyBindingActionManager keyBindingActionManager){
		this.keyBindingActionManager = keyBindingActionManager;
	}
	
	public void keyPressed(KeyEvent event) {
		KeyBinding keyBinding = new KeyBinding();
		keyBinding.setKey(event.keyCode);
		keyBinding.setMask(event.stateMask);
		this.keyBindingActionManager.processKeyBinding(keyBinding);
	}
	
	public void keyReleased(KeyEvent evt) {
		//not implemented
	}
}
