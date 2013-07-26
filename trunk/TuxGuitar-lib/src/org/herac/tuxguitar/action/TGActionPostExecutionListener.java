package org.herac.tuxguitar.action;

public interface TGActionPostExecutionListener {
	
	public void doPostExecution(String id, TGActionContext context) throws TGActionException;
	
}
