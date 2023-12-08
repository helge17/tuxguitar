package org.herac.tuxguitar.android.midi.port;

import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.util.TGContext;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin{
	
	private static final String MODULE_ID = "tuxguitar-android-midi";
	
	protected MidiOutputPortProvider createProvider(TGContext context) {
		return new MidiOutputPortProviderImpl(context);
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}
