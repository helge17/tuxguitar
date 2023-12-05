package org.herac.tuxguitar.player.impl.midiport.coreaudio;

import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.util.TGContext;

public class MidiPortReaderPlugin extends TGMidiOutputPortProviderPlugin{

	public static final String MODULE_ID = "tuxguitar-coreaudio";
	
	protected MidiOutputPortProvider createProvider(TGContext context) {
		return new MidiPortReaderCoreAudio();
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}	
}
