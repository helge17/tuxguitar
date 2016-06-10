package swtimpl.event;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.herac.tuxguitar.ui.event.UIFocusEvent;
import org.herac.tuxguitar.ui.event.UIFocusGainedListener;
import org.herac.tuxguitar.ui.event.UIFocusGainedListenerManager;
import org.herac.tuxguitar.ui.event.UIFocusLostListener;
import org.herac.tuxguitar.ui.event.UIFocusLostListenerManager;

import swtimpl.SWTComponent;

public class SWTFocusListenerManager implements FocusListener {
	
	private SWTComponent<?> control;
	private UIFocusGainedListenerManager focusGainedListener;
	private UIFocusLostListenerManager focusLostListener;
	
	public SWTFocusListenerManager(SWTComponent<?> control) {
		this.control = control;
		this.focusGainedListener = new UIFocusGainedListenerManager();
		this.focusLostListener = new UIFocusLostListenerManager();
	}
	
	public boolean isEmpty() {
		return (this.focusGainedListener.isEmpty() && this.focusLostListener.isEmpty());
	}
	
	public void addListener(UIFocusGainedListener listener) {
		this.focusGainedListener.addListener(listener);
	}
	
	public void addListener(UIFocusLostListener listener) {
		this.focusLostListener.addListener(listener);
	}
	
	public void removeListener(UIFocusGainedListener listener) {
		this.focusGainedListener.removeListener(listener);
	}
	
	public void removeListener(UIFocusLostListener listener) {
		this.focusLostListener.removeListener(listener);
	}

	public void focusGained(FocusEvent e) {
		this.focusGainedListener.onFocusGained(new UIFocusEvent(this.control));
	}

	public void focusLost(FocusEvent e) {
		this.focusLostListener.onFocusLost(new UIFocusEvent(this.control));
	}
}
