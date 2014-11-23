package org.herac.tuxguitar.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.action.TGActionException;

public class TGEventHandler {
	
	private Object lock;
	private List listeners;
	
	public TGEventHandler() {
		this.lock = new Object();
		this.listeners = new ArrayList();
	}
	
	public void processEvent(TGEvent event) throws TGActionException{
		List listeners = new ArrayList();
		synchronized (this.lock) {
			listeners.addAll(this.listeners);
		}
		
		Iterator it = listeners.iterator();
		while( it.hasNext() ){
			((TGEventListener) it.next()).processEvent(event);
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
