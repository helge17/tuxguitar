package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UISelectionListenerManager implements UISelectionListener {
	
	private List<UISelectionListener> listeners;
	
	public UISelectionListenerManager() {
		this.listeners = new ArrayList<UISelectionListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UISelectionListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UISelectionListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onSelect(UISelectionEvent event) {
		List<UISelectionListener> listeners = new ArrayList<UISelectionListener>(this.listeners);
		for(UISelectionListener listener : listeners) {
			listener.onSelect(event);
		}
	}
}
