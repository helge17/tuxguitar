package org.herac.tuxguitar.player.impl.midiport.winmm;

import org.herac.tuxguitar.gui.system.plugins.base.TGMidiPortProviderPlugin;
import org.herac.tuxguitar.player.base.MidiPortProvider;


public class MidiPortProviderPlugin extends TGMidiPortProviderPlugin{

	private MidiPortProviderImpl portReader;
	
	protected MidiPortProvider getProvider() {
		if(this.portReader == null){
			this.portReader = new MidiPortProviderImpl();
		}
		return this.portReader;
	}

	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}

	public String getDescription() {		
		return "WinMM output plugin";
	}

	public String getName() {
		return "WinMM output plugin";
	}

	public String getVersion() {
		return "1.0-rc3";
	}

}
