package org.herac.tuxguitar.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.action.TGActionException;

public class TGEventHandler {
	
	private List listeners;
	
	public TGEventHandler() {
		this.listeners = new ArrayList();
	}
	
	public void processEvent(TGEvent event) throws TGActionException{
		Iterator it = this.listeners.iterator();
		while( it.hasNext() ){
			((TGEventListener) it.next()).processEvent(event);
		}
	}
	
	public void addListener(TGEventListener listener){
		if(!this.listeners.contains(listener)){
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(TGEventListener listener){
		if( this.listeners.contains(listener) ){
			this.listeners.remove(listener);
		}
	}
}
