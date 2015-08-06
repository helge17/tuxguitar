package org.herac.tuxguitar.app.action.impl.settings;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;

public class TGReloadMidiDevicesAction extends TGActionBase {
	
	public static final String NAME = "action.system.reload-midi-devices";
	
	public TGReloadMidiDevicesAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		Boolean force = Boolean.TRUE.equals(context.getAttribute(TGReloadSettingsAction.ATTRIBUTE_FORCE));
		TGConfigManager config = TGConfigManager.getInstance(this.getContext());
		MidiPlayer midiPlayer = MidiPlayer.getInstance(getContext());
		
		String midiSequencer = config.getStringValue(TGConfigKeys.MIDI_SEQUENCER);
		if( force || !midiPlayer.isSequencerOpen(midiSequencer) ){
			midiPlayer.openSequencer(midiSequencer, false);
		}
		String midiPort = config.getStringValue(TGConfigKeys.MIDI_PORT);
		if( force || !midiPlayer.isOutputPortOpen(midiPort) ){
			midiPlayer.openOutputPort(midiPort, false);
		}
	}
}