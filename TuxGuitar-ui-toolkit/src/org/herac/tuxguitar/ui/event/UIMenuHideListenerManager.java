package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIMenuHideListenerManager implements UIMenuHideListener {
	
	private List<UIMenuHideListener> listeners;
	
	public UIMenuHideListenerManager() {
		this.listeners = new ArrayList<UIMenuHideListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIMenuHideListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIMenuHideListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onMenuHide(UIMenuEvent event) {
		List<UIMenuHideListener> listeners = new ArrayList<UIMenuHideListener>(this.listeners);
		for(UIMenuHideListener listener : listeners) {
			listener.onMenuHide(event);
		}
	}
}
