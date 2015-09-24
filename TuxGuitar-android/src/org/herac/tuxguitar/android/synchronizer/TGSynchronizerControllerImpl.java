package org.herac.tuxguitar.android.synchronizer;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer.TGSynchronizerController;

import android.os.Handler;

public class TGSynchronizerControllerImpl implements TGSynchronizerController {
	
	private TGContext context;
	private Handler handler;
	
	public TGSynchronizerControllerImpl(TGContext context) {
		this.context = context;
		this.handler = new Handler();
	}
	
	@Override
	public void executeLater(Runnable target) {
		final TGRunnable runnable = new TGRunnable(this.context, target);
		Thread thread = new Thread( new Runnable() {
			public void run() {
				TGSynchronizerControllerImpl.this.handler.post(runnable);
			}
		});
		thread.start();
	}
}
