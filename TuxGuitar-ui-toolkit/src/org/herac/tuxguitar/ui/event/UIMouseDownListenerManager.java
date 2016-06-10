package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIMouseDownListenerManager implements UIMouseDownListener {
	
	private List<UIMouseDownListener> listeners;
	
	public UIMouseDownListenerManager() {
		this.listeners = new ArrayList<UIMouseDownListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIMouseDownListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIMouseDownListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onMouseDown(UIMouseEvent event) {
		List<UIMouseDownListener> listeners = new ArrayList<UIMouseDownListener>(this.listeners);
		for(UIMouseDownListener listener : listeners) {
			listener.onMouseDown(event);
		}
	}
}
