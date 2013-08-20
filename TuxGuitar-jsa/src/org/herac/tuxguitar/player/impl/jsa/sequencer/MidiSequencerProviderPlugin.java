package org.herac.tuxguitar.player.impl.jsa.sequencer;

import org.herac.tuxguitar.player.base.MidiSequencerProvider;
import org.herac.tuxguitar.player.impl.jsa.MidiPlugin;
import org.herac.tuxguitar.player.plugin.TGMidiSequencerProviderPlugin;

public class MidiSequencerProviderPlugin extends TGMidiSequencerProviderPlugin{

	protected MidiSequencerProvider getProvider() {
		return new MidiSequencerProviderImpl();
	}
	
	public String getModuleId() {
		return MidiPlugin.MODULE_ID;
	}
}
