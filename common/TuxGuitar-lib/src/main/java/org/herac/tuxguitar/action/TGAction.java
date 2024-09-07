package org.herac.tuxguitar.action;

public interface TGAction {
	
	public void execute(TGActionContext context) throws TGActionException;
}
