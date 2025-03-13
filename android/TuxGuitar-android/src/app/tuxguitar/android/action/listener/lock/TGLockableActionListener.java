package app.tuxguitar.android.action.listener.lock;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionErrorEvent;
import app.tuxguitar.action.TGActionEvent;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionInterceptor;
import app.tuxguitar.action.TGActionPostExecutionEvent;
import app.tuxguitar.action.TGActionPreExecutionEvent;
import app.tuxguitar.android.action.listener.thread.TGSyncThreadAction;
import app.tuxguitar.editor.TGEditorManager;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.util.TGContext;

public class TGLockableActionListener extends TGSyncThreadAction implements TGActionInterceptor, TGEventListener {

	private static final String INTERCEPTOR_BY_PASS = TGLockableActionListener.class.getSimpleName() + "-interceptor-by-pass";

	private List<String> actionIds;

	public TGLockableActionListener(TGContext context){
		super(context);

		this.actionIds = new ArrayList<String>();
	}

	public boolean containsActionId(String id) {
		return this.actionIds.contains(id);
	}

	public void addActionId(String id) {
		this.actionIds.add(id);
	}

	public void removeActionId(String id) {
		this.actionIds.remove(id);
	}

	public void checkForLock(String actionId) {
		if( this.containsActionId(actionId)) {
			TGEditorManager.getInstance(this.getContext()).lock();
		}
	}

	public void checkForUnlock(String actionId) {
		if( this.containsActionId(actionId)) {
			TGEditorManager.getInstance(this.getContext()).unlock();
		}
	}

	public void setByPassInContext(TGActionContext context, boolean byPass) {
		context.setAttribute(INTERCEPTOR_BY_PASS, byPass);
	}

	public boolean isByPassInContext(TGActionContext context) {
		return Boolean.TRUE.equals(context.getAttribute(INTERCEPTOR_BY_PASS));
	}

	public void processEvent(TGEvent event) {
		if( TGActionPreExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.checkForLock((String) event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID));
		}
		else if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.checkForUnlock((String) event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID));
		}
		else if( TGActionErrorEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.checkForUnlock((String) event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID));
		}
	}

	public boolean intercept(String id, TGActionContext context) throws TGActionException {
		if(!this.isByPassInContext(context) && this.isUiThread() && this.containsActionId(id)) {
			this.runInUiThread(id, context);

			return true;
		}
		return false;
	}

	public void executeInterceptedAction(String actionId, TGActionContext context) {
		TGEditorManager editor = TGEditorManager.getInstance(this.getContext());
		if (editor.tryLock()) {
			try {
				this.setByPassInContext(context, true);

				super.executeInterceptedAction(actionId, context);

				this.setByPassInContext(context, false);
			} finally {
				editor.unlock();
			}
		} else {
			// try later
			this.runInUiThread(actionId, context);
		}
	}
}