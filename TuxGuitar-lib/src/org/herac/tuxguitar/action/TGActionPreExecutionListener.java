package org.herac.tuxguitar.action;

public interface TGActionPreExecutionListener {
	
	public void doPreExecution(String id, TGActionContext context) throws TGActionException;
	
}
