package org.herac.tuxguitar.ui.jfx.event;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.herac.tuxguitar.ui.event.UIFocusEvent;
import org.herac.tuxguitar.ui.event.UIFocusGainedListener;
import org.herac.tuxguitar.ui.event.UIFocusGainedListenerManager;
import org.herac.tuxguitar.ui.event.UIFocusLostListener;
import org.herac.tuxguitar.ui.event.UIFocusLostListenerManager;
import org.herac.tuxguitar.ui.jfx.widget.JFXEventReceiver;

public class JFXFocusListenerManager implements ChangeListener<Boolean> {
	
	private JFXEventReceiver<?> control;
	private UIFocusGainedListenerManager focusGainedListener;
	private UIFocusLostListenerManager focusLostListener;
	
	public JFXFocusListenerManager(JFXEventReceiver<?> control) {
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
	
	public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		if(!this.control.isIgnoreEvents()) {
			UIFocusEvent uiFocusEvent = new UIFocusEvent(this.control);
			if( Boolean.TRUE.equals(newValue)) {
				this.focusGainedListener.onFocusGained(uiFocusEvent);
			} else {
				this.focusLostListener.onFocusLost(uiFocusEvent);
			}
		}
	}
}
