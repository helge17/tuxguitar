package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UILinkListenerManager implements UILinkListener {
	
	private List<UILinkListener> listeners;
	
	public UILinkListenerManager() {
		this.listeners = new ArrayList<UILinkListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UILinkListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UILinkListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onLinkSelect(UILinkEvent event) {
		List<UILinkListener> listeners = new ArrayList<UILinkListener>(this.listeners);
		for(UILinkListener listener : listeners) {
			listener.onLinkSelect(event);
		}
	}
}
