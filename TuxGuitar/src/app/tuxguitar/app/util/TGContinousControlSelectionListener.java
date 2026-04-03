package app.tuxguitar.app.util;

import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.util.TGException;
import app.tuxguitar.util.TGSynchronizer;

// to handle actions on continuous controls: knob, scale,...
// waits for control to be stable, then trigger action

public class TGContinousControlSelectionListener implements UISelectionListener, Runnable {

	private static final int DEFAULT_STABILIZATION_DELAY_MS = 500;
	private long stabilizationDelayMs;
	private TGContinuousControl control;
	private long startTime;
	private boolean running;
	private Object mutex = new Object();

	public TGContinousControlSelectionListener(TGContinuousControl control, int stabilisationDelayMs){
		this.control = control;
		this.stabilizationDelayMs = stabilisationDelayMs;
	}
	public TGContinousControlSelectionListener(TGContinuousControl control){
		this(control, DEFAULT_STABILIZATION_DELAY_MS);
	}

	@Override
	public void onSelect(UISelectionEvent event) {
		synchronized (this.mutex) {

			if(!this.running){
				this.running = true;
				// Start the thread.
				Thread thread = new Thread(this);
				thread.start();
			}

			this.startTime = System.currentTimeMillis();
			// notify the thread
			this.mutex.notifyAll();
		}
	}

	@Override
	public void run() {
		try {
			long timeToWait = this.stabilizationDelayMs;

			while( this.running ){
				synchronized (this.mutex) {
					timeToWait = ((this.startTime + this.stabilizationDelayMs) - System.currentTimeMillis());
					this.running = ( timeToWait > 0 );
					if( this.running ){
						this.mutex.wait(timeToWait);
					}
				}
			}
			// now, control is stabilized
			this.doActionSynchronized();

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void doActionSynchronized() throws Throwable {
		TGSynchronizer.getInstance(this.control.getContext()).executeLater(new Runnable() {
			public void run() throws TGException {
				doAction();
			}
		});
	}

	private void doAction(){
		this.control.doActionWhenStable();
	}
}
