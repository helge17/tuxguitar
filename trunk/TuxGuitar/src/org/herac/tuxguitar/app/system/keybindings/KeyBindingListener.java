package org.herac.tuxguitar.app.system.keybindings;

import org.herac.tuxguitar.ui.event.UIKeyEvent;
import org.herac.tuxguitar.ui.event.UIKeyPressedListener;

public class KeyBindingListener implements UIKeyPressedListener {
	
	private KeyBindingActionManager keyBindingActionManager;
	
	public KeyBindingListener(KeyBindingActionManager keyBindingActionManager){
		this.keyBindingActionManager = keyBindingActionManager;
	}
	
	public void onKeyPressed(UIKeyEvent event) {
		this.keyBindingActionManager.processKeyBinding(event.getKeyConvination());
	}
}
