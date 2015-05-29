package org.herac.tuxguitar.event;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionException;

public class TGEventHandler {
	
	private Object lock;
	private List<TGEventListener> listeners;
	
	public TGEventHandler() {
		this.lock = new Object();
		this.listeners = new ArrayList<TGEventListener>();
	}
	
	public void processEvent(TGEvent event) throws TGActionException{
		List<TGEventListener> listeners = new ArrayList<TGEventListener>();
		synchronized (this.lock) {
			listeners.addAll(this.listeners);
		}
		
		for(TGEventListener tgEventListener : listeners){
			tgEventListener.processEvent(event);
		}
	}
	
	public void addListener(TGEventListener listener){
		synchronized (this.lock) {
			if(!this.listeners.contains(listener)){
				this.listeners.add(listener);
			}
		}
	}
	
	public void removeListener(TGEventListener listener){
		synchronized (this.lock) {
			if( this.listeners.contains(listener) ){
				this.listeners.remove(listener);
			}
		}
	}
}
