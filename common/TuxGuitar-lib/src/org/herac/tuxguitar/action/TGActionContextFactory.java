package org.herac.tuxguitar.action;

public interface TGActionContextFactory {
	
	public TGActionContext createActionContext() throws TGActionException;
	
}
