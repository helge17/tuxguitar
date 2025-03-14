package app.tuxguitar.player.impl.jsa.sequencer;

import app.tuxguitar.player.base.MidiSequencerProvider;
import app.tuxguitar.player.impl.jsa.MidiPlugin;
import app.tuxguitar.player.plugin.TGMidiSequencerProviderPlugin;
import app.tuxguitar.util.TGContext;

public class MidiSequencerProviderPlugin extends TGMidiSequencerProviderPlugin{

	protected MidiSequencerProvider createProvider(TGContext context) {
		return new MidiSequencerProviderImpl();
	}

	public String getModuleId() {
		return MidiPlugin.MODULE_ID;
	}
}
