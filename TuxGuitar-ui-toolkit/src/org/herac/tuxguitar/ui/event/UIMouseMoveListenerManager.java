package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIMouseMoveListenerManager implements UIMouseMoveListener {
	
	private List<UIMouseMoveListener> listeners;
	
	public UIMouseMoveListenerManager() {
		this.listeners = new ArrayList<UIMouseMoveListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIMouseMoveListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIMouseMoveListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onMouseMove(UIMouseEvent event) {
		List<UIMouseMoveListener> listeners = new ArrayList<UIMouseMoveListener>(this.listeners);
		for(UIMouseMoveListener listener : listeners) {
			listener.onMouseMove(event);
		}
	}
}
