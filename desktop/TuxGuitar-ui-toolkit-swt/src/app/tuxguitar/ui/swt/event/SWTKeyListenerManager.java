package app.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import app.tuxguitar.ui.event.UIKeyEvent;
import app.tuxguitar.ui.event.UIKeyPressedListener;
import app.tuxguitar.ui.event.UIKeyPressedListenerManager;
import app.tuxguitar.ui.event.UIKeyReleasedListener;
import app.tuxguitar.ui.event.UIKeyReleasedListenerManager;
import app.tuxguitar.ui.swt.resource.SWTKey;
import app.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTKeyListenerManager implements KeyListener {

	private SWTEventReceiver<?> control;
	private SWTKey key;
	private UIKeyPressedListenerManager keyPressedListener;
	private UIKeyReleasedListenerManager keyReleasedListener;

	public SWTKeyListenerManager(SWTEventReceiver<?> control) {
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
		if(!this.control.isIgnoreEvents()) {
			this.keyPressedListener.onKeyPressed(new UIKeyEvent(this.control, this.key.getCombination(e.keyCode, e.stateMask, e.character)));
		}
	}

	public void keyReleased(KeyEvent e) {
		if(!this.control.isIgnoreEvents()) {
			this.keyReleasedListener.onKeyReleased(new UIKeyEvent(this.control, this.key.getCombination(e.keyCode, e.stateMask, e.character)));
		}
	}
}
