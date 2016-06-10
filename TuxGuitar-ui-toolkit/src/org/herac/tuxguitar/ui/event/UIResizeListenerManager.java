package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIResizeListenerManager implements UIResizeListener {
	
	private List<UIResizeListener> listeners;
	
	public UIResizeListenerManager() {
		this.listeners = new ArrayList<UIResizeListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIResizeListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIResizeListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onResize(UIResizeEvent event) {
		List<UIResizeListener> listeners = new ArrayList<UIResizeListener>(this.listeners);
		for(UIResizeListener listener : listeners) {
			listener.onResize(event);
		}
	}
}
