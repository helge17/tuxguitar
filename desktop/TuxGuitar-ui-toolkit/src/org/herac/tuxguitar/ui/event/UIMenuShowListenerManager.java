package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIMenuShowListenerManager implements UIMenuShowListener {
	
	private List<UIMenuShowListener> listeners;
	
	public UIMenuShowListenerManager() {
		this.listeners = new ArrayList<UIMenuShowListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIMenuShowListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIMenuShowListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onMenuShow(UIMenuEvent event) {
		List<UIMenuShowListener> listeners = new ArrayList<UIMenuShowListener>(this.listeners);
		for(UIMenuShowListener listener : listeners) {
			listener.onMenuShow(event);
		}
	}
}
