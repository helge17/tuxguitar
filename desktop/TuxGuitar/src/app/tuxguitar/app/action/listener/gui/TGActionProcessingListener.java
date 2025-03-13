package app.tuxguitar.app.action.listener.gui;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionErrorEvent;
import app.tuxguitar.action.TGActionPostExecutionEvent;
import app.tuxguitar.action.TGActionPreExecutionEvent;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.util.TGContext;

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
