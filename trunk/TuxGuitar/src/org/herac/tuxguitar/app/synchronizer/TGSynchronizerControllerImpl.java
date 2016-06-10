package org.herac.tuxguitar.app.synchronizer;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.ui.UIApplication;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer.TGSynchronizerController;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class TGSynchronizerControllerImpl implements TGSynchronizerController {
	
	private TGContext context;
	private TGApplication application;
	
	public TGSynchronizerControllerImpl(TGContext context) {
		this.context = context;
		this.application = TGApplication.getInstance(context);
	}
	
	public void executeLater(final Runnable runnable) {
		new Thread(new Runnable() {
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
		}).start();
	}
}
