package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIMouseDoubleClickListenerManager implements UIMouseDoubleClickListener {
	
	private List<UIMouseDoubleClickListener> listeners;
	
	public UIMouseDoubleClickListenerManager() {
		this.listeners = new ArrayList<UIMouseDoubleClickListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIMouseDoubleClickListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIMouseDoubleClickListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onMouseDoubleClick(UIMouseEvent event) {
		List<UIMouseDoubleClickListener> listeners = new ArrayList<UIMouseDoubleClickListener>(this.listeners);
		for(UIMouseDoubleClickListener listener : listeners) {
			listener.onMouseDoubleClick(event);
		}
	}
}
