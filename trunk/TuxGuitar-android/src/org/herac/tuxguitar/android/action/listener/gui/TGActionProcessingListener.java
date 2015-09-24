package org.herac.tuxguitar.android.action.listener.gui;

import org.herac.tuxguitar.action.TGActionErrorEvent;
import org.herac.tuxguitar.action.TGActionEvent;
import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.action.TGActionPreExecutionEvent;
import org.herac.tuxguitar.android.action.impl.gui.TGFinishAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

import android.app.ProgressDialog;

public class TGActionProcessingListener implements TGEventListener {
	
	private TGActivity activity;
	private boolean finished;
	private ProgressDialog dialog;
	private Integer level;
	
	public TGActionProcessingListener(TGActivity activity){
		this.activity = activity;
		this.resetLevel();
		this.createProgressDialog();
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
	
	public void createProgressDialog() {
		this.dialog = new ProgressDialog(this.activity);
		this.dialog.setMessage("Processing");
		this.dialog.setIndeterminate(true);
		this.dialog.setCancelable(true);
	}
	
	public void dismissProgressDialog() {
		if(!this.finished) {
			this.dialog.dismiss();
		}
	}
	
	public void showProgressDialog() {
		if(!this.finished) {
			this.dialog.show();
		}
	}
	
	public void hideProgressDialog() {
		if(!this.finished) {
			this.dialog.hide();
		}
	}
	
	public void updateProgressDialogVisibility(boolean processing) {
		if( processing ) {
			this.showProgressDialog();
		} else {
			this.hideProgressDialog();
		}
	}
	
	public void processEvent(final boolean processing) {
		if( this.level == 0 ) {
			TGSynchronizer.getInstance(this.activity.findContext()).executeLater(new Runnable() {
				public void run() throws TGException {
					TGActionProcessingListener.this.updateProgressDialogVisibility(processing);
				}
			});
		}
	}
	
	public void finish() {
		this.dismissProgressDialog();
		this.finished = true;
	}
	
	public boolean isFinishAction(TGEvent event) {
		return (TGFinishAction.NAME.equals(event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID)));
	}
	
	public void processEvent(TGEvent event) {
		if( this.isFinishAction(event) ) {
			this.finish();
		}
		else if(!this.finished) {
			if( TGActionPreExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
				this.processEvent(true);
				this.increaseLevel();
			}
			else if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
				this.decreaseLevel();
				this.processEvent(false);
			}
			else if( TGActionErrorEvent.EVENT_TYPE.equals(event.getEventType()) ) {
				this.resetLevel();
				this.processEvent(false);
			}
		}
	}
}
