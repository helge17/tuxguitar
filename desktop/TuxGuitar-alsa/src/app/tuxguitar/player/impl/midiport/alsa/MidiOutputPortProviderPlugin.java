package app.tuxguitar.player.impl.midiport.alsa;

import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import app.tuxguitar.util.TGContext;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin{

	private static final String MODULE_ID = "tuxguitar-alsa";

	protected MidiOutputPortProvider createProvider(TGContext context) {
		return new MidiOutputPortProviderImpl();
	}

	public String getModuleId() {
		return MODULE_ID;
	}
}
