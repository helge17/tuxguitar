package org.herac.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.herac.tuxguitar.ui.event.UIKeyEvent;
import org.herac.tuxguitar.ui.event.UIKeyPressedListener;
import org.herac.tuxguitar.ui.event.UIKeyPressedListenerManager;
import org.herac.tuxguitar.ui.event.UIKeyReleasedListener;
import org.herac.tuxguitar.ui.event.UIKeyReleasedListenerManager;
import org.herac.tuxguitar.ui.swt.SWTComponent;
import org.herac.tuxguitar.ui.swt.resource.SWTKey;

public class SWTKeyListenerManager implements KeyListener {
	
	private SWTComponent<?> control;
	private SWTKey key;
	private UIKeyPressedListenerManager keyPressedListener;
	private UIKeyReleasedListenerManager keyReleasedListener;
	
	public SWTKeyListenerManager(SWTComponent<?> control) {
		this.control = control;
		this.key = new SWTKey();
		this.keyPressedListener = new UIKeyPressedListenerManager();
		this.keyReleasedListener = new UIKeyReleasedListenerManager();
	}
	
	public boolean isEmpty() {
		return (this.keyPressedListener.isEmpty() && this.keyReleasedListener.isEmpty());
	}
	
	public void addListener(UIKeyPressedListener listener) {
		this.keyPressedListener.addListener(listener);
	}
	
	public void addListener(UIKeyReleasedListener listener) {
		this.keyReleasedListener.addListener(listener);
	}
	
	public void removeListener(UIKeyPressedListener listener) {
		this.keyPressedListener.removeListener(listener);
	}
	
	public void removeListener(UIKeyReleasedListener listener) {
		this.keyReleasedListener.removeListener(listener);
	}

	public void keyPressed(KeyEvent e) {
		this.keyPressedListener.onKeyPressed(new UIKeyEvent(this.control, this.key.getConvination(e.keyCode, e.stateMask)));
	}

	public void keyReleased(KeyEvent e) {
		this.keyReleasedListener.onKeyReleased(new UIKeyEvent(this.control, this.key.getConvination(e.keyCode, e.stateMask)));
	}
}
