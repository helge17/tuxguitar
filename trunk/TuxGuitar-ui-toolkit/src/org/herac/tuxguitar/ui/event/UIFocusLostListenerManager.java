package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIFocusLostListenerManager implements UIFocusLostListener {
	
	private List<UIFocusLostListener> listeners;
	
	public UIFocusLostListenerManager() {
		this.listeners = new ArrayList<UIFocusLostListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIFocusLostListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIFocusLostListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onFocusLost(UIFocusEvent event) {
		List<UIFocusLostListener> listeners = new ArrayList<UIFocusLostListener>(this.listeners);
		for(UIFocusLostListener listener : listeners) {
			listener.onFocusLost(event);
		}
	}
}
