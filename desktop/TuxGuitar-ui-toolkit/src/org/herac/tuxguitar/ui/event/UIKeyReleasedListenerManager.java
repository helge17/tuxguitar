package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIKeyReleasedListenerManager implements UIKeyReleasedListener {
	
	private List<UIKeyReleasedListener> listeners;
	
	public UIKeyReleasedListenerManager() {
		this.listeners = new ArrayList<UIKeyReleasedListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIKeyReleasedListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIKeyReleasedListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onKeyReleased(UIKeyEvent event) {
		List<UIKeyReleasedListener> listeners = new ArrayList<UIKeyReleasedListener>(this.listeners);
		for(UIKeyReleasedListener listener : listeners) {
			listener.onKeyReleased(event);
		}
	}
}
