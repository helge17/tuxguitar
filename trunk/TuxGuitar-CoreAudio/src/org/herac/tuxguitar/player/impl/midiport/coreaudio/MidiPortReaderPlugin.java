package org.herac.tuxguitar.player.impl.midiport.coreaudio;

import org.herac.tuxguitar.app.system.plugins.base.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;

public class MidiPortReaderPlugin extends TGMidiOutputPortProviderPlugin{

	public static final String MODULE_ID = "tuxguitar-coreaudio";
	
	protected MidiOutputPortProvider getProvider() {
		return new MidiPortReaderCoreAudio();
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}	
}
