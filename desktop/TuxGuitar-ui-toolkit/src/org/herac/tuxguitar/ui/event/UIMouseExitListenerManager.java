package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIMouseExitListenerManager implements UIMouseExitListener {
	
	private List<UIMouseExitListener> listeners;
	
	public UIMouseExitListenerManager() {
		this.listeners = new ArrayList<UIMouseExitListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIMouseExitListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIMouseExitListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onMouseExit(UIMouseEvent event) {
		List<UIMouseExitListener> listeners = new ArrayList<UIMouseExitListener>(this.listeners);
		for(UIMouseExitListener listener : listeners) {
			listener.onMouseExit(event);
		}
	}
}
