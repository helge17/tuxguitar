package org.herac.tuxguitar.player.impl.midiport.winmm;

import org.herac.tuxguitar.gui.system.plugins.base.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin{
	
	private MidiOutputPortProviderImpl portReader;
	
	protected MidiOutputPortProvider getProvider() {
		if(this.portReader == null){
			this.portReader = new MidiOutputPortProviderImpl();
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
		return "1.0";
	}
}
