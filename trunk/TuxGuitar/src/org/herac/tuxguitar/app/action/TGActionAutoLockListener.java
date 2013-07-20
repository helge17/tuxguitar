package org.herac.tuxguitar.app.action;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionPreExecutionListener;

public class TGActionAutoLockListener implements TGActionPreExecutionListener {
	
	private TGActionAdapterManager manager;
	
	public TGActionAutoLockListener(TGActionAdapterManager manager){
		this.manager = manager;
	}
	
	public void doPreExecution(String id, TGActionContext context) throws TGActionException {
		if( this.manager.getAutoLockActionIds().hasActionId(id) ) {
			TGActionLock.lock();
		}
	}
}
