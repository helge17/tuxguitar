package org.herac.tuxguitar.android.action.listener.gui;

import org.herac.tuxguitar.action.TGActionErrorEvent;
import org.herac.tuxguitar.action.TGActionEvent;
import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.action.TGActionPreExecutionEvent;
import org.herac.tuxguitar.android.action.TGActionAsyncProcessErrorEvent;
import org.herac.tuxguitar.android.action.TGActionAsyncProcessFinishEvent;
import org.herac.tuxguitar.android.action.TGActionAsyncProcessStartEvent;
import org.herac.tuxguitar.android.action.impl.gui.TGFinishAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.view.processing.TGActionProcessingController;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;

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

	public TGActionProcessingListener(TGActivity activity) {
		this.controller = new TGActionProcessingController(activity);
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
