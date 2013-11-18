package org.herac.tuxguitar.jack.synthesizer.settings;

import org.herac.tuxguitar.player.base.MidiPlayerListener;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class JackMidiPlayerListener implements MidiPlayerListener{
	
	private JackChannelSettingsDialog jackChannelSettingsDialog;
	
	public JackMidiPlayerListener(JackChannelSettingsDialog jackChannelSettingsDialog){
		this.jackChannelSettingsDialog = jackChannelSettingsDialog;
	}
	
	public void notifyStarted() {
		this.updateControlsSynchronized();
	}

	public void notifyStopped() {
		this.updateControlsSynchronized();
	}

	public void notifyCountDownStarted() {
		// Not implemented
	}

	public void notifyCountDownStopped() {
		// Not implemented
	}

	public void notifyLoop() {
		// Not implemented
	}
	
	public void updateControls(){
		this.jackChannelSettingsDialog.updateControls();
	}
	
	public void updateControlsSynchronized(){
		TGSynchronizer.instance().executeLater(new TGSynchronizer.TGRunnable() {
			public void run() throws TGException {
				updateControls();
			}
		});
	}
}
