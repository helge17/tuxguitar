package org.herac.tuxguitar.midi.synth.ui;

import org.herac.tuxguitar.editor.util.TGDelayedProcess;
import org.herac.tuxguitar.editor.util.TGProcess;
import org.herac.tuxguitar.util.TGContext;

public class TGSynthDialogDelayedCallback implements TGAudioProcessorUICallback {
	
	private static final Integer TIMEOUT = 500;
	
	private Runnable onProcessorUpdated;
	private TGProcess onProcessorUpdatedDelayed;
	
	public TGSynthDialogDelayedCallback(TGContext context, Runnable onProcessorUpdated) {
		this.onProcessorUpdated = onProcessorUpdated;
		this.onProcessorUpdatedDelayed = new TGDelayedProcess(context, TIMEOUT, this.onProcessorUpdated);
	}
	
	public void onChange(boolean notifyImmediately) {
		if( notifyImmediately ) {
			this.onProcessorUpdated.run();
		} else {
			this.onProcessorUpdatedDelayed.process();
		}
	}
}
