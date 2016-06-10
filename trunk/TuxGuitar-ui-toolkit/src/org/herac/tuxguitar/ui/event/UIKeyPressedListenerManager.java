package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIKeyPressedListenerManager implements UIKeyPressedListener {
	
	private List<UIKeyPressedListener> listeners;
	
	public UIKeyPressedListenerManager() {
		this.listeners = new ArrayList<UIKeyPressedListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIKeyPressedListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIKeyPressedListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onKeyPressed(UIKeyEvent event) {
		List<UIKeyPressedListener> listeners = new ArrayList<UIKeyPressedListener>(this.listeners);
		for(UIKeyPressedListener listener : listeners) {
			listener.onKeyPressed(event);
		}
	}
}
