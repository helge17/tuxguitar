package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIPaintListenerManager implements UIPaintListener {
	
	private List<UIPaintListener> listeners;
	
	public UIPaintListenerManager() {
		this.listeners = new ArrayList<UIPaintListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIPaintListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIPaintListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onPaint(UIPaintEvent event) {
		List<UIPaintListener> listeners = new ArrayList<UIPaintListener>(this.listeners);
		for(UIPaintListener listener : listeners) {
			listener.onPaint(event);
		}
	}
}
