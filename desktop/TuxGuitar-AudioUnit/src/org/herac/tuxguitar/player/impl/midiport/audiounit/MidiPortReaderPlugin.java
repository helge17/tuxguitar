package org.herac.tuxguitar.player.impl.midiport.audiounit;

import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.util.TGContext;

public class MidiPortReaderPlugin extends TGMidiOutputPortProviderPlugin{
	
	public static final String MODULE_ID = "tuxguitar-audiounit";
	
	protected MidiOutputPortProvider createProvider(TGContext context) {
		return new MidiPortReaderAudioUnit();
	}

	public String getModuleId() {
		return MODULE_ID;
	}
}
