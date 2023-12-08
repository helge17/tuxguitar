package org.herac.tuxguitar.app.action.listener.cache;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.action.TGActionPreExecutionEvent;
import org.herac.tuxguitar.app.action.TGActionAdapterManager;
import org.herac.tuxguitar.app.action.TGActionMap;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;

public class TGUpdateListener implements TGEventListener {
	
	private TGActionAdapterManager manager;
	private TGActionMap<TGUpdateController> controllers;
	
	public TGUpdateListener(TGActionAdapterManager manager){
		this.manager = manager;
		this.controllers = new TGActionMap<TGUpdateController>();
	}
	
	public TGActionMap<TGUpdateController> getControllers() {
		return controllers;
	}

	public void processUpdate(String actionId, TGActionContext actionContext) {
		TGUpdateController controller = this.controllers.get(actionId);
		if( controller != null ) {
			controller.update(this.manager.getContext(), actionContext);
		}
	}
	
	public void processPreExecution(TGActionContext actionContext) {
		TGUpdateContext updateContext = TGUpdateContext.getInstance(this.manager.getContext(), actionContext);
		
		if( updateContext.getLevel().equals(0) ) {
			updateContext.getBuffer().clear();
		}
		
		updateContext.incrementLevel();
	}
	
	public void processPostExecution(String actionId, TGActionContext actionContext) {
		TGUpdateContext updateContext = TGUpdateContext.getInstance(this.manager.getContext(), actionContext);
		updateContext.decrementLevel();
		
		this.processUpdate(actionId, actionContext);
		
		if( updateContext.getLevel().equals(0) ) {
			updateContext.getBuffer().apply(actionContext);
		}
	}
	
	public void processPreExecution(TGEvent event) {
		TGActionContext actionContext = event.getAttribute(TGEvent.ATTRIBUTE_SOURCE_CONTEXT);
		
		this.processPreExecution(actionContext);
	}
	
	public void processPostExecution(TGEvent event) {
		String actionId = event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_ACTION_ID);
		TGActionContext actionContext = event.getAttribute(TGEvent.ATTRIBUTE_SOURCE_CONTEXT);
		
		this.processPostExecution(actionId, actionContext);
	}
	
	public void processEvent(final TGEvent event) {
		if( TGActionPreExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processPreExecution(event);
		}
		else if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processPostExecution(event);
		}
	}
}
