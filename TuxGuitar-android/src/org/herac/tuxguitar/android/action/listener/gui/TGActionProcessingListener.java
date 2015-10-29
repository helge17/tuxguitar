package org.herac.tuxguitar.android.action.listener.gui;

import org.herac.tuxguitar.action.TGActionErrorEvent;
import org.herac.tuxguitar.action.TGActionEvent;
import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.action.TGActionPreExecutionEvent;
import org.herac.tuxguitar.android.action.impl.gui.TGFinishAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.view.processing.TGActionProcessingController;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;

public class TGActionProcessingListener implements TGEventListener {
	
	private TGActionProcessingController controller;
	private Integer level;
	
	public TGActionProcessingListener(TGActivity activity){
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
			if( TGActionPreExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
				this.processEvent(true);
				this.increaseLevel();
			}
			else if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
				this.decreaseLevel();
				this.processEvent(false);
			}
			else if( TGActionErrorEvent.EVENT_TYPE.equals(event.getEventType()) ) {
				this.decreaseLevel();
				this.processEvent(false);
			}
		}
	}
}
