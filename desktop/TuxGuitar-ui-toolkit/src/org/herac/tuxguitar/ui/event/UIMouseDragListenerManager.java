package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIMouseDragListenerManager implements UIMouseDragListener {
	
	private List<UIMouseDragListener> listeners;
	
	public UIMouseDragListenerManager() {
		this.listeners = new ArrayList<UIMouseDragListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIMouseDragListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIMouseDragListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onMouseDrag(UIMouseEvent event) {
		List<UIMouseDragListener> listeners = new ArrayList<UIMouseDragListener>(this.listeners);
		for(UIMouseDragListener listener : listeners) {
			listener.onMouseDrag(event);
		}
	}
}
