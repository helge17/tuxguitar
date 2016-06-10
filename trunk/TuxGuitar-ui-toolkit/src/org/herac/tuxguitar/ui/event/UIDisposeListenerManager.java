package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIDisposeListenerManager implements UIDisposeListener {
	
	private List<UIDisposeListener> listeners;
	
	public UIDisposeListenerManager() {
		this.listeners = new ArrayList<UIDisposeListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIDisposeListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIDisposeListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onDispose(UIDisposeEvent event) {
		List<UIDisposeListener> listeners = new ArrayList<UIDisposeListener>(this.listeners);
		for(UIDisposeListener listener : listeners) {
			listener.onDispose(event);
		}
	}
}
