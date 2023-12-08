package org.herac.tuxguitar.android.view.util;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGSyncProcess implements TGProcess {
	
	private TGContext context;
	private Runnable runnable;
	private boolean pending;
	
	public TGSyncProcess(TGContext context, Runnable runnable) {
		this.context = context;
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
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				processRunnable();
			}
		});
	}
}
