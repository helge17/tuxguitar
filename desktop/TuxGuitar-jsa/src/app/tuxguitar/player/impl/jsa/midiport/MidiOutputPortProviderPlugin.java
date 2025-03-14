package app.tuxguitar.player.impl.jsa.midiport;

import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.player.impl.jsa.MidiPlugin;
import app.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import app.tuxguitar.util.TGContext;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin{

	protected MidiOutputPortProvider createProvider(TGContext context) {
		return new MidiPortProviderImpl(context);
	}

	public String getModuleId() {
		return MidiPlugin.MODULE_ID;
	}
}
