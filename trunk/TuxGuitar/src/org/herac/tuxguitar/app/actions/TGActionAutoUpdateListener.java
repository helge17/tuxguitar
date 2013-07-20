package org.herac.tuxguitar.app.actions;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionPostExecutionListener;
import org.herac.tuxguitar.app.TuxGuitar;

public class TGActionAutoUpdateListener implements TGActionPostExecutionListener {
	
	private TGActionAdapterManager manager;
	
	public TGActionAutoUpdateListener(TGActionAdapterManager manager){
		this.manager = manager;
	}
	
	public void doPostExecution(String id, TGActionContext context) throws TGActionException {
		TuxGuitar.instance().updateCache( this.manager.getAutoUpdateActionIds().hasActionId(id) );
	}
}
