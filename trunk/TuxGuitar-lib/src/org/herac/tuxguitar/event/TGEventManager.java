package org.herac.tuxguitar.event;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.action.TGActionException;

public class TGEventManager {
	
	private static TGEventManager instance;
	
	private Map eventHandlers;
	
	private TGEventManager() {
		this.eventHandlers = new HashMap();
	}
	
	public static TGEventManager getInstance(){
		synchronized (TGEventManager.class) {
			if( instance == null ){
				instance = new TGEventManager();
			}
			return instance;
		}
	}
	
	public void addListener(String eventType, TGEventListener listener){
		if( eventType != null ) {
			this.findEventHandler(eventType).addListener(listener);
		}
	}
	
	public void removeListener(String eventType, TGEventListener listener){
		if( eventType != null ) {
			this.findEventHandler(eventType).removeListener(listener);
		}
	}
	
	public void fireEvent(TGEvent event) throws TGActionException {
		TGEventHandler handler = this.findEventHandler(event.getEventType());
		if( handler != null ) {
			handler.processEvent(event);
		}
	}
	
	public TGEventHandler findEventHandler(String eventType) {
		if( eventType == null ) {
			return null;
		}
		if( this.eventHandlers.containsKey(eventType) ) {
			return (TGEventHandler)this.eventHandlers.get(eventType);
		}
		this.eventHandlers.put(eventType, new TGEventHandler());
		
		return this.findEventHandler(eventType);
	}
}
