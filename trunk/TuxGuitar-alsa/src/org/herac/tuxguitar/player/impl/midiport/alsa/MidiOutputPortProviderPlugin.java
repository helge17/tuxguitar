package org.herac.tuxguitar.player.impl.midiport.alsa;

import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.util.TGContext;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin{
	
	private static final String MODULE_ID = "tuxguitar-alsa";
	
	protected MidiOutputPortProvider createProvider(TGContext context) {
		return new MidiOutputPortProviderImpl();
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}
