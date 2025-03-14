package app.tuxguitar.player.impl.midiport.coreaudio;

import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import app.tuxguitar.util.TGContext;

public class MidiPortReaderPlugin extends TGMidiOutputPortProviderPlugin{

	public static final String MODULE_ID = "tuxguitar-coreaudio";

	protected MidiOutputPortProvider createProvider(TGContext context) {
		return new MidiPortReaderCoreAudio();
	}

	public String getModuleId() {
		return MODULE_ID;
	}
}
