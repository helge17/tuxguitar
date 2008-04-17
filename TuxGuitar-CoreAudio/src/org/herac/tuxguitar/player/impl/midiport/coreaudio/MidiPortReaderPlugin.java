package org.herac.tuxguitar.player.impl.midiport.coreaudio;

import org.herac.tuxguitar.gui.system.plugins.base.TGMidiPortProviderPlugin;
import org.herac.tuxguitar.player.base.MidiPortProvider;

public class MidiPortReaderPlugin extends TGMidiPortProviderPlugin{

	protected MidiPortProvider getProvider() {
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
		return "1.0-rc3";
	}
	
}
