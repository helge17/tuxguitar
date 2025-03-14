package app.tuxguitar.android.synchronizer;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer.TGSynchronizerController;

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
