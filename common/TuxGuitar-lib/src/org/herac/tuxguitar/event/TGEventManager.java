package org.herac.tuxguitar.event;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGEventManager {
	
	private Map<String, TGEventHandler> eventHandlers;
	
	private TGEventManager() {
		this.eventHandlers = new HashMap<String, TGEventHandler>();
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
	
	public void fireEvent(TGEvent event) throws TGEventException {
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
	
	public void clear() {
		this.eventHandlers.clear();
	}
	
	public static TGEventManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGEventManager.class.getName(), new TGSingletonFactory<TGEventManager>() {
			public TGEventManager createInstance(TGContext context) {
				return new TGEventManager();
			}
		});
	}
}
