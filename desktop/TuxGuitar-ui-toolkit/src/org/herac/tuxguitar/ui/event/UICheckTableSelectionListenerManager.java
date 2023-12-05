package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UICheckTableSelectionListenerManager<T> implements UICheckTableSelectionListener<T> {
	
	private List<UICheckTableSelectionListener<T>> listeners;
	
	public UICheckTableSelectionListenerManager() {
		this.listeners = new ArrayList<UICheckTableSelectionListener<T>>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UICheckTableSelectionListener<T> listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UICheckTableSelectionListener<T> listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onSelect(UICheckTableSelectionEvent<T> event) {
		List<UICheckTableSelectionListener<T>> listeners = new ArrayList<UICheckTableSelectionListener<T>>(this.listeners);
		for(UICheckTableSelectionListener<T> listener : listeners) {
			listener.onSelect(event);
		}
	}
}
