package app.tuxguitar.editor.util;

import app.tuxguitar.editor.TGEditorManager;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGException;
import app.tuxguitar.util.TGLock;
import app.tuxguitar.util.TGSynchronizer;

public class TGSyncProcessLocked implements TGProcess {

	private TGContext context;
	private Runnable runnable;
	private boolean pending;

	public TGSyncProcessLocked(TGContext context, Runnable runnable) {
		this.context = context;
		this.runnable = runnable;
		this.pending = false;
	}

	public void process() {
		if(!this.pending) {
			this.pending = true;
			this.processLaterLocked();
		}
	}

	private void processRunnable() {
		this.pending = false;
		this.runnable.run();
	}

	private void processLaterLocked() throws TGException {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				TGLock lock = findLockControl();
				if( lock.tryLock() ) {
					try {
						processRunnable();
					} finally {
						lock.unlock();
					}
				} else {
					processLaterLocked();
				}
			}
		});
	}

	private TGLock findLockControl() {
		return TGEditorManager.getInstance(TGSyncProcessLocked.this.context).getLockControl();
	}
}
