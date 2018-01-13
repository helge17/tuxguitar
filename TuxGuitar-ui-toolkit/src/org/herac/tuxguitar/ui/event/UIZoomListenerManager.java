package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIZoomListenerManager implements UIZoomListener {
	
	private List<UIZoomListener> listeners;
	
	public UIZoomListenerManager() {
		this.listeners = new ArrayList<UIZoomListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIZoomListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIZoomListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onZoom(UIZoomEvent event) {
		List<UIZoomListener> listeners = new ArrayList<UIZoomListener>(this.listeners);
		for(UIZoomListener listener : listeners) {
			listener.onZoom(event);
		}
	}
}
