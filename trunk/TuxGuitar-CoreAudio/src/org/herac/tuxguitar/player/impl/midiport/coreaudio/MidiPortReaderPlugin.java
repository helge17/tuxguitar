package org.herac.tuxguitar.player.impl.midiport.coreaudio;

import org.herac.tuxguitar.gui.system.plugins.base.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;

public class MidiPortReaderPlugin extends TGMidiOutputPortProviderPlugin{

	protected MidiOutputPortProvider getProvider() {
		return new MidiPortReaderCoreAudio();
	}

	public String getAuthor() {
		return "Auria";
	}

	public String getDescription() {		
		return "Core Audio output plugin";
	}

	public String getName() {
		return "Core Audio output plugin";
	}

	public String getVersion() {
		return "1.0";
	}
	
}
