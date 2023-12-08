package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIFocusGainedListenerManager implements UIFocusGainedListener {
	
	private List<UIFocusGainedListener> listeners;
	
	public UIFocusGainedListenerManager() {
		this.listeners = new ArrayList<UIFocusGainedListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIFocusGainedListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIFocusGainedListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onFocusGained(UIFocusEvent event) {
		List<UIFocusGainedListener> listeners = new ArrayList<UIFocusGainedListener>(this.listeners);
		for(UIFocusGainedListener listener : listeners) {
			listener.onFocusGained(event);
		}
	}
}
