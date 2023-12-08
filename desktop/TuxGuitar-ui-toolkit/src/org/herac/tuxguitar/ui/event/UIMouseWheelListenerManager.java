package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIMouseWheelListenerManager implements UIMouseWheelListener {
	
	private List<UIMouseWheelListener> listeners;
	
	public UIMouseWheelListenerManager() {
		this.listeners = new ArrayList<UIMouseWheelListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIMouseWheelListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIMouseWheelListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onMouseWheel(UIMouseWheelEvent event) {
		List<UIMouseWheelListener> listeners = new ArrayList<UIMouseWheelListener>(this.listeners);
		for(UIMouseWheelListener listener : listeners) {
			listener.onMouseWheel(event);
		}
	}
}
