package org.herac.tuxguitar.android.view.processing;

import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.util.TGLock;

public class TGActionProcessingController {
	
	private static final long PROCESSING_DELAY = 100;
	
	private TGLock lock;
	private TGActionProcessingView view;
	private TGActionProcessingModel model;
	
	private boolean running;
	
	public TGActionProcessingController(TGActivity activity){
		this.lock = new TGLock();
		this.view = new TGActionProcessingView(activity);
		this.model = new TGActionProcessingModel();
	}
	
	public void update(boolean processing) {
		try {
			this.lock.lock();
			
			if(!this.isFinished()) {
				this.model.update(processing);
				
				if(!this.running && this.model.isProcessing() ) {
					this.running = true;
					this.start();
				}
			}
		} finally {
			this.lock.unlock();
		}
	}
	
	public void process() {
		while( this.running ) {
			if (this.lock.tryLock()) {
				try {
					if(!this.view.isUpdating() ) {
						if( this.model.isProcessing() && !this.view.isVisible() ) {
							if((this.model.getProcessingTime() + PROCESSING_DELAY) < System.currentTimeMillis()) {
								this.view.postUpdateProgressDialog(true);
							}
						}
						
						if(!this.model.isProcessing() && this.view.isVisible() ) {
							this.view.postUpdateProgressDialog(false);
						}
					}
					
					this.running = (this.model.isProcessing() || this.view.isUpdating() || this.view.isVisible());
				} finally {
					this.lock.unlock();
				}
			}
			
			Thread.yield();
		}
	}
	
	public void finish() {
		try {
			this.lock.lock();
			
			this.running = false;
			
			this.view.destroy();
		} finally {
			this.lock.unlock();
		}
	}
	
	public void start() {
		new Thread(new Runnable() {
			public void run() {
				process();
			}
		}).start();
	}

	public boolean isFinished() {
		return this.view.isDestroyed();
	}
}
