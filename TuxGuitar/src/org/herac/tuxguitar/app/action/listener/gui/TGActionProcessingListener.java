package org.herac.tuxguitar.app.action.listener.gui;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionErrorEvent;
import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.action.TGActionPreExecutionEvent;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGContext;

public class TGActionProcessingListener implements TGEventListener {
	
	public static final String ATTRIBUTE_BY_PASS = "byPassProcessingListener";
	
	private TGContext context;
	private Integer level;
	
	public TGActionProcessingListener(TGContext context){
		this.context = context;
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
	
	public void showProcessing() {
		TGWindow.getInstance(this.context).loadBusyCursor();
	}
	
	public void hideProcessing() {
		TGWindow.getInstance(this.context).loadDefaultCursor();
	}
	
	public void updateProcessingVisibility(boolean processing) {
		if( processing ) {
			this.showProcessing();
		} else {
			this.hideProcessing();
		}
	}
	
	public boolean isByPassProcessing(TGEvent event) {
		TGActionContext actionContext = event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_SOURCE_CONTEXT);
		
		return Boolean.TRUE.equals(actionContext.getAttribute(ATTRIBUTE_BY_PASS));
	}
	
	public void processEvent(final boolean processing) {
		if( this.level == 0 ) {
			this.updateProcessingVisibility(processing);
		}
	}
	
	public void processEvent(TGEvent event) {
		if(!this.isByPassProcessing(event) ) {
			
			synchronized (TGActionProcessingListener.class) {
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
}
