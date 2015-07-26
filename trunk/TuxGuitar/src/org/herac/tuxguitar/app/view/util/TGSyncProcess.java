package org.herac.tuxguitar.app.view.util;

import org.herac.tuxguitar.util.TGSynchronizer;

public class TGSyncProcess {
	
	private boolean pending;
	private Runnable runnable;
	
	public TGSyncProcess(Runnable runnable) {
		this.runnable = runnable;
		this.pending = false;
	}
	
	public void process() {
		if(!this.pending) {
			this.pending = true;
			this.processLater();
		}
	}
	
	private void processRunnable() {
		this.pending = false;
		this.runnable.run();
	}
	
	private void processLater() {
		TGSynchronizer.instance().executeLater(new Runnable() {
			public void run() {
				processRunnable();
			}
		});
	}
}
