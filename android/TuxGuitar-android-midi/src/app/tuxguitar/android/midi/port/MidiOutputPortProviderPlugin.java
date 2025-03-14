package app.tuxguitar.android.midi.port;

import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import app.tuxguitar.util.TGContext;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin{

	private static final String MODULE_ID = "tuxguitar-android-midi";

	protected MidiOutputPortProvider createProvider(TGContext context) {
		return new MidiOutputPortProviderImpl(context);
	}

	public String getModuleId() {
		return MODULE_ID;
	}
}
