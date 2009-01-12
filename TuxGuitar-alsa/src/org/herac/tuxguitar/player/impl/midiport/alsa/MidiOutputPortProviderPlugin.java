package org.herac.tuxguitar.player.impl.midiport.alsa;

import org.herac.tuxguitar.gui.system.plugins.base.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin{
	
	protected MidiOutputPortProvider getProvider() {
		return new MidiOutputPortProviderImpl();
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
		return "1.0";
	}
	
}
