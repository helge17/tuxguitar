package app.tuxguitar.android.action.listener.gui;

import app.tuxguitar.action.TGActionErrorEvent;
import app.tuxguitar.action.TGActionEvent;
import app.tuxguitar.action.TGActionPostExecutionEvent;
import app.tuxguitar.action.TGActionPreExecutionEvent;
import app.tuxguitar.android.action.TGActionAsyncProcessErrorEvent;
import app.tuxguitar.android.action.TGActionAsyncProcessFinishEvent;
import app.tuxguitar.android.action.TGActionAsyncProcessStartEvent;
import app.tuxguitar.android.action.impl.gui.TGFinishAction;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.view.processing.TGActionProcessingController;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.util.TGContext;

public class TGActionProcessingListener implements TGEventListener {

	private static final String[] PRE_EXEC_EVENTS = {
		TGActionPreExecutionEvent.EVENT_TYPE,
		TGActionAsyncProcessStartEvent.EVENT_TYPE
	};

	private static final String[] POST_EXEC_EVENTS = {
		TGActionPostExecutionEvent.EVENT_TYPE,
		TGActionAsyncProcessFinishEvent.EVENT_TYPE
	};

	private static final String[] ERROR_EVENTS = {
		TGActionErrorEvent.EVENT_TYPE,
		TGActionAsyncProcessErrorEvent.EVENT_TYPE
	};

	private TGActionProcessingController controller;
	private Integer level;

	public TGActionProcessingListener(TGContext context, TGActivity activity) {
		this.controller = new TGActionProcessingController(context, activity);
		this.resetLevel();
	}

	public void resetLevel() {
		this.level = 0;
	}

	public void increaseLevel() {
		this.level ++;
	}

	public void decreaseLevel() {
		this.level --;
	}

	public void finish() {
		this.controller.finish();
	}

	public boolean isFinishAction(TGEvent event) {
		return (TGFinishAction.NAME.equals(event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID)));
	}

	public boolean isEventType(TGEvent event, String[] eventTypes) {
		for(String eventType : eventTypes) {
			if( eventType.equals(event.getEventType())) {
				return true;
			}
		}
		return false;
	}

	public void processEvent(final boolean processing) {
		if( this.level == 0 ) {
			this.controller.update(processing);
		}
	}

	public void processEvent(TGEvent event) {
		if( this.isFinishAction(event) ) {
			this.finish();
		}
		else if(!this.controller.isFinished()) {

			synchronized (TGActionProcessingListener.class) {
				if( this.isEventType(event, PRE_EXEC_EVENTS)) {
					this.processEvent(true);
					this.increaseLevel();
				}
				else if( this.isEventType(event, POST_EXEC_EVENTS)) {
					this.decreaseLevel();
					this.processEvent(false);
				}
				else if( this.isEventType(event, ERROR_EVENTS)) {
					this.decreaseLevel();
					this.processEvent(false);
				}
			}
		}
	}
}
