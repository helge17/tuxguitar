package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIMouseUpListenerManager implements UIMouseUpListener {
	
	private List<UIMouseUpListener> listeners;
	
	public UIMouseUpListenerManager() {
		this.listeners = new ArrayList<UIMouseUpListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIMouseUpListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIMouseUpListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onMouseUp(UIMouseEvent event) {
		List<UIMouseUpListener> listeners = new ArrayList<UIMouseUpListener>(this.listeners);
		for(UIMouseUpListener listener : listeners) {
			listener.onMouseUp(event);
		}
	}
}
