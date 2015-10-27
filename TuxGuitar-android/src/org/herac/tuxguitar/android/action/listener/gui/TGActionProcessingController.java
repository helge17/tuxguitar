package org.herac.tuxguitar.android.action.listener.gui;

import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

import android.app.ProgressDialog;

public class TGActionProcessingController {
	
	private static final long PROCESSING_DELAY = 150;
	
	private Object lock;
	private TGActivity activity;
	private ProgressDialog dialog;
	
	private boolean dialogOpen;
	private boolean finished;
	private boolean updating;
	private boolean running;
	private boolean processing;
	private long processingTime;
	
	public TGActionProcessingController(TGActivity activity){
		this.activity = activity;
		this.lock = new Object();
		this.createProgressDialog();
		this.start();
	}
	
	private void createProgressDialog() {
		this.dialog = new ProgressDialog(this.activity);
		this.dialog.setMessage("Processing");
		this.dialog.setIndeterminate(true);
		this.dialog.setCancelable(true);
	}
	
	private void dismissProgressDialog() {
		this.dialog.dismiss();
	}
	
	private void updateProgressDialog(boolean visible) {
		if(!this.finished && !this.activity.isFinishing()) {
			if( visible ) {
				this.dialog.show();
			} else {
				this.dialog.hide();
			}
			this.dialogOpen = visible;
		}
	}
	
	private void postUpdateProgressDialog(final boolean visible) {
		this.updating = true;
		
		TGSynchronizer.getInstance(this.activity.findContext()).executeLater(new Runnable() {
			public void run() throws TGException {
				TGActionProcessingController.this.updateProgressDialog(visible);
				TGActionProcessingController.this.updating = false;
			}
		});
	}
	
	public void update(boolean processing) {
		synchronized (this.lock) {
			this.processing = processing;
			this.processingTime = System.currentTimeMillis();
			
			if(!this.running && this.processing ) {
				this.running = true;
				this.start();
			}
		}
	}
	
	public void process() {
		while( this.running ) {
			synchronized (this.lock) {
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
				
				this.running = (this.updating || this.processing || this.dialogOpen);
			}
			
			Thread.yield();
		}
	}
	
	public void finish() {
		synchronized (this.lock) {
			this.finished = true;
			this.running = false;
		}
		
		this.dismissProgressDialog();
	}
	
	public void start() {
		new Thread(new Runnable() {
			public void run() {
				process();
			}
		}).start();
	}

	public boolean isFinished() {
		return finished;
	}
}
