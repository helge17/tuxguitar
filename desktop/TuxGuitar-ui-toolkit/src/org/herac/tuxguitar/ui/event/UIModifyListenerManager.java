package org.herac.tuxguitar.ui.event;

import java.util.ArrayList;
import java.util.List;

public class UIModifyListenerManager implements UIModifyListener {
	
	private List<UIModifyListener> listeners;
	
	public UIModifyListenerManager() {
		this.listeners = new ArrayList<UIModifyListener>();
	}
	
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	public void addListener(UIModifyListener listener) {
		if(!this.listeners.contains(listener) ) {
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(UIModifyListener listener) {
		if( this.listeners.contains(listener) ) {
			this.listeners.remove(listener);
		}
	}
	
	public void onModify(UIModifyEvent event) {
		List<UIModifyListener> listeners = new ArrayList<UIModifyListener>(this.listeners);
		for(UIModifyListener listener : listeners) {
			listener.onModify(event);
		}
	}
}
