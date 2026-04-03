package app.tuxguitar.app.system.keybindings;

import app.tuxguitar.ui.event.UIKeyEvent;
import app.tuxguitar.ui.event.UIKeyPressedListener;

public class KeyBindingListener implements UIKeyPressedListener {

	private KeyBindingActionManager keyBindingActionManager;

	public KeyBindingListener(KeyBindingActionManager keyBindingActionManager){
		this.keyBindingActionManager = keyBindingActionManager;
	}

	public void onKeyPressed(UIKeyEvent event) {
		this.keyBindingActionManager.processKeyBinding(event.getKeyCombination());
	}
}
