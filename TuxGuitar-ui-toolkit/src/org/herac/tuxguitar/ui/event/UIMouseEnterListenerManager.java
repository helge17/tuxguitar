package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIMouseEnterListenerManager implements UIMouseEnterListener {
	
	private List<UIMouseEnterListener> listeners;
	
	public UIMouseEnterListenerManager() {
		this.listeners = new ArrayList<UIMouseEnterListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIMouseEnterListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIMouseEnterListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onMouseEnter(UIMouseEvent event) {
		List<UIMouseEnterListener> listeners = new ArrayList<UIMouseEnterListener>(this.listeners);
		for(UIMouseEnterListener listener : listeners) {
			listener.onMouseEnter(event);
		}
	}
}
