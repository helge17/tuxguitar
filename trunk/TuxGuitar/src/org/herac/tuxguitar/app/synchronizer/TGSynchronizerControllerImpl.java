package org.herac.tuxguitar.app.synchronizer;

import org.eclipse.swt.widgets.Display;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer.TGSynchronizerController;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class TGSynchronizerControllerImpl implements TGSynchronizerController {
	
	private TGContext context;
	private Display display;
	
	public TGSynchronizerControllerImpl(TGContext context, Display display) {
		this.context = context;
		this.display = display;
	}
	
	public void executeLater(final Runnable runnable) {
		new Thread(new Runnable() {
			public void run() {
				final Display display = TGSynchronizerControllerImpl.this.display;
				if( display != null && !display.isDisposed()){
					display.asyncExec(new Runnable() {
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
