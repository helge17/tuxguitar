package org.herac.tuxguitar.android.midimaster.port;

import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.util.TGContext;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin{
	
	private static final String MODULE_ID = "tuxguitar-android-midimaster";
	
	protected MidiOutputPortProvider createProvider(TGContext context) {
		return new MidiOutputPortProviderImpl();
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}
