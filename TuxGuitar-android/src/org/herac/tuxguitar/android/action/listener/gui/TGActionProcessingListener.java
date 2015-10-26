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
	
	private static final long PROCESSING_DELAY = 250;
	
	private TGActivity activity;
	private ProgressDialog dialog;
	private Integer level;
	private boolean dialogOpen;
	private boolean finished;
	private boolean updating;
	private boolean processing;
	private long processingTime;
	
	public TGActionProcessingListener(TGActivity activity){
		this.activity = activity;
		this.resetLevel();
		this.createProgressDialog();
		this.start();
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
	
	public void updateProgressDialog(boolean visible) {
		if(!this.finished) {
			if( visible ) {
				this.dialog.show();
			} else {
				this.dialog.hide();
			}
			this.dialogOpen = visible;
		}
	}
	
	public void postUpdateProgressDialog(final boolean visible) {
		this.updating = true;
		
		TGSynchronizer.getInstance(this.activity.findContext()).executeLater(new Runnable() {
			public void run() throws TGException {
				TGActionProcessingListener.this.updateProgressDialog(visible);
				TGActionProcessingListener.this.updating = false;
			}
		});
	}
	
	public void start() {
		new Thread(new Runnable() {
			public void run() {
				process();
			}
		}).start();
	}
	
	public void process() {
		while(!this.finished) {
			if(!this.updating ) {
				if( this.processing && !this.dialogOpen ) {
					if((this.processingTime + PROCESSING_DELAY) < System.currentTimeMillis()) {
						this.postUpdateProgressDialog(true);
					}
				}
				
				if(!this.processing && this.dialogOpen ) {
					this.postUpdateProgressDialog(false);
				}
			}
			Thread.yield();
		}
	}
	
	public void finish() {
		this.finished = true;
		this.dismissProgressDialog();
	}
	
	public boolean isFinishAction(TGEvent event) {
		return (TGFinishAction.NAME.equals(event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID)));
	}
	
	public void processEvent(final boolean processing) {
		if( this.level == 0 ) {
			this.processing = processing;
			this.processingTime = System.currentTimeMillis();
		}
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
				this.decreaseLevel();
				this.processEvent(false);
			}
		}
	}
}
