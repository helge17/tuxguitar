package org.herac.tuxguitar.android.action;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.util.error.TGErrorHandler;

public class TGActionAsyncProcess implements TGErrorHandler {

	private TGActionBase action;
	private TGActionContext actionContext;

	public TGActionAsyncProcess(TGActionBase action, TGActionContext actionContext) {
		this.action = action;
		this.actionContext = actionContext;
	}

	public TGActionBase getAction() {
		return this.action;
	}

	public TGActionContext getActionContext() {
		return this.actionContext;
	}

	public TGEventManager getEventManager() {
		return TGEventManager.getInstance(this.getAction().getContext());
	}

	public void onStart() {
		getEventManager().fireEvent(new TGActionAsyncProcessStartEvent(this.getAction().getName(), this.actionContext));
	}

	public void onFinish() {
		getEventManager().fireEvent(new TGActionAsyncProcessFinishEvent(this.getAction().getName(), this.actionContext));
	}

	public void handleError(Throwable throwable) {
		getEventManager().fireEvent(new TGActionAsyncProcessErrorEvent(this.getAction().getName(), this.actionContext, throwable));
	}
}
