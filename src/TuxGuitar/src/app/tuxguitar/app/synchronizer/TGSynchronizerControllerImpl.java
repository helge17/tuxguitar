package app.tuxguitar.app.synchronizer;

import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.thread.TGThreadManager;
import app.tuxguitar.ui.UIApplication;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGException;
import app.tuxguitar.util.TGSynchronizer.TGSynchronizerController;
import app.tuxguitar.util.error.TGErrorManager;

public class TGSynchronizerControllerImpl implements TGSynchronizerController {

	private TGContext context;
	private TGApplication application;

	public TGSynchronizerControllerImpl(TGContext context) {
		this.context = context;
		this.application = TGApplication.getInstance(context);
	}

	public void executeLater(final Runnable runnable) {
		TGThreadManager.getInstance(this.context).start(new Runnable() {
			public void run() {
				UIApplication application = TGSynchronizerControllerImpl.this.application.getApplication();
				if( application != null && !application.isDisposed()){
					application.runInUiThread(new Runnable() {
						public void run() throws TGException {
							try {
								runnable.run();
							} catch (Throwable throwable) {
								TGErrorManager.getInstance(TGSynchronizerControllerImpl.this.context).handleError(throwable);
							}
						}
					});
				}
			}
		});
	}
}
