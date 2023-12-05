package org.herac.tuxguitar.jack.synthesizer.settings;

import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.player.base.MidiPlayerEvent;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class JackMidiPlayerListener implements TGEventListener {
	
	private TGContext context;
	private JackChannelSettingsDialog jackChannelSettingsDialog;
	
	public JackMidiPlayerListener(TGContext context, JackChannelSettingsDialog jackChannelSettingsDialog){
		this.context = context;
		this.jackChannelSettingsDialog = jackChannelSettingsDialog;
	}

	public void updateControls(){
		this.jackChannelSettingsDialog.updateControls();
	}
	
	public void updateControlsSynchronized(){
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() throws TGException {
				updateControls();
			}
		});
	}
	
	public void processStarted() {
		this.updateControlsSynchronized();
	}

	public void processStopped() {
		this.updateControlsSynchronized();
	}
	
	public void processEvent(TGEvent event) {
		if( MidiPlayerEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			int type = ((Integer)event.getAttribute(MidiPlayerEvent.PROPERTY_NOTIFICATION_TYPE)).intValue();
			if( type == MidiPlayerEvent.NOTIFY_STARTED ){
				this.processStarted();
			} else if( type == MidiPlayerEvent.NOTIFY_STOPPED ){
				this.processStopped();
			}
		}
	}
}
