package org.herac.tuxguitar.app.actions;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionPostExecutionListener;

public class TGActionAutoUnlockListener implements TGActionPostExecutionListener {
	
	private TGActionAdapterManager manager;
	
	public TGActionAutoUnlockListener(TGActionAdapterManager manager){
		this.manager = manager;
	}
	
	public void doPostExecution(String id, TGActionContext context) throws TGActionException {
		if( this.manager.getAutoUnlockActionIds().hasActionId(id) ) {
			TGActionLock.unlock();
		}
	}
}
