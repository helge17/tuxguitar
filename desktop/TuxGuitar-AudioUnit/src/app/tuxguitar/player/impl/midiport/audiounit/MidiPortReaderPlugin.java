package app.tuxguitar.player.impl.midiport.audiounit;

import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import app.tuxguitar.util.TGContext;

public class MidiPortReaderPlugin extends TGMidiOutputPortProviderPlugin{

	public static final String MODULE_ID = "tuxguitar-audiounit";

	protected MidiOutputPortProvider createProvider(TGContext context) {
		return new MidiPortReaderAudioUnit();
	}

	public String getModuleId() {
		return MODULE_ID;
	}
}
