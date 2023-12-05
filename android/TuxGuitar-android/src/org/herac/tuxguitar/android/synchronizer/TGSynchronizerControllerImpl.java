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
		this.handler.post(new TGRunnable(this.context, target));
	}
}
