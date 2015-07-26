package org.herac.tuxguitar.event;

import java.util.ArrayList;
import java.util.List;

public class TGEventHandler {
	
	private Object lock;
	private List<TGEventListener> listeners;
	
	public TGEventHandler() {
		this.lock = new Object();
		this.listeners = new ArrayList<TGEventListener>();
	}
	
	public void processEvent(TGEvent event) throws TGEventException {
		List<TGEventListener> listeners = new ArrayList<TGEventListener>();
		synchronized (this.lock) {
			listeners.addAll(this.listeners);
		}
		
		try {
			for(TGEventListener tgEventListener : listeners){
				tgEventListener.processEvent(event);
			}
		} catch (Throwable e) {
			throw new TGEventException(e);
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
