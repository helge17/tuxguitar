package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UICloseListenerManager implements UICloseListener {
	
	private List<UICloseListener> listeners;
	
	public UICloseListenerManager() {
		this.listeners = new ArrayList<UICloseListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UICloseListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UICloseListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onClose(UICloseEvent event) {
		List<UICloseListener> listeners = new ArrayList<UICloseListener>(this.listeners);
		for(UICloseListener listener : listeners) {
			listener.onClose(event);
		}
	}
}
