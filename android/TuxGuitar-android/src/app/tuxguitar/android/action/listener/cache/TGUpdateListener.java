package app.tuxguitar.android.action.listener.cache;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionErrorEvent;
import app.tuxguitar.action.TGActionPostExecutionEvent;
import app.tuxguitar.action.TGActionPreExecutionEvent;
import app.tuxguitar.android.action.TGActionAdapterManager;
import app.tuxguitar.android.action.TGActionMap;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.util.TGAbstractContext;

public class TGUpdateListener implements TGEventListener {

	private TGActionAdapterManager manager;
	private TGActionMap<TGUpdateController> controllers;
	private TGUpdateBuffer buffer;
	private Integer level;

	public TGUpdateListener(TGActionAdapterManager manager){
		this.manager = manager;
		this.controllers = new TGActionMap<TGUpdateController>();
		this.buffer = new TGUpdateBuffer(manager.getContext());
		this.level = 0;
	}

	public TGUpdateBuffer getBuffer() {
		return buffer;
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

	public void processUpdate(TGEvent event) {
		String actionId = event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_ACTION_ID);
		TGActionContext actionContext = event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_SOURCE_CONTEXT);

		this.processUpdate(actionId, actionContext);
	}

	public void processPreExecution() {
		if( this.level == 0 ) {
			this.getBuffer().clear();
		}
		this.level ++;
	}

	public void processPostExecution(TGEvent event) {
		this.level --;
		this.processUpdate(event);
		if( this.level == 0 ) {
			this.getBuffer().apply((TGAbstractContext) event.getAttribute(TGEvent.ATTRIBUTE_SOURCE_CONTEXT));
		}
	}

	public void processError() {
		this.level = 0;
		this.getBuffer().clear();
	}

	public void processEvent(final TGEvent event) {
		if( TGActionPreExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processPreExecution();
		}
		else if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processPostExecution(event);
		}
		else if( TGActionErrorEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processError();
		}
	}
}
