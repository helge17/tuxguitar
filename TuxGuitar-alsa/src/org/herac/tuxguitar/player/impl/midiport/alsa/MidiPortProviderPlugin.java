package org.herac.tuxguitar.player.impl.midiport.alsa;

import org.herac.tuxguitar.gui.system.plugins.base.TGMidiPortProviderPlugin;
import org.herac.tuxguitar.player.base.MidiPortProvider;

public class MidiPortProviderPlugin extends TGMidiPortProviderPlugin{

	protected MidiPortProvider getProvider() {
		return new MidiPortProviderImpl();
	}

	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}

	public String getDescription() {		
		return "ALSA output plugin";
	}

	public String getName() {
		return "ALSA output plugin";
	}

	public String getVersion() {
		return "1.0-rc3";
	}
	
}
