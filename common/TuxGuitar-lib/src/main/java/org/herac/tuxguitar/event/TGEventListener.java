package org.herac.tuxguitar.event;

public interface TGEventListener {
	
	public void processEvent(TGEvent event) throws TGEventException;
}
