package app.tuxguitar.app.action.listener.cache;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionEvent;
import app.tuxguitar.action.TGActionPostExecutionEvent;
import app.tuxguitar.action.TGActionPreExecutionEvent;
import app.tuxguitar.app.action.TGActionAdapterManager;
import app.tuxguitar.app.action.TGActionMap;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;

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

	public void processPreExecution(String actionId, TGActionContext actionContext) {
		if( this.controllers.get(actionId) != null ) {
			TGUpdateContext updateContext = TGUpdateContext.getInstance(this.manager.getContext(), actionContext);

			if( updateContext.getLevel().equals(0) ) {
				updateContext.getBuffer().clear();
			}

			updateContext.incrementLevel();
		}
	}

	public void processPostExecution(String actionId, TGActionContext actionContext) {
		if( this.controllers.get(actionId) != null ) {
			TGUpdateContext updateContext = TGUpdateContext.getInstance(this.manager.getContext(), actionContext);
			updateContext.decrementLevel();

			this.processUpdate(actionId, actionContext);

			if( updateContext.getLevel().equals(0) ) {
				updateContext.getBuffer().apply(actionContext);
			}
		}
	}

	public void processPreExecution(TGEvent event) {
		String actionId = event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID);
		TGActionContext actionContext = event.getAttribute(TGEvent.ATTRIBUTE_SOURCE_CONTEXT);

		this.processPreExecution(actionId, actionContext);
	}

	public void processPostExecution(TGEvent event) {
		String actionId = event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID);
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
