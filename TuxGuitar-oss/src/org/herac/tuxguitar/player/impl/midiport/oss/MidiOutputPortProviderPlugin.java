package org.herac.tuxguitar.player.impl.midiport.oss;

import org.herac.tuxguitar.app.system.plugins.base.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin {
	
	private MidiOutputPortProviderImpl portReader;
	
	public MidiOutputPortProvider getProvider() {
		if( this.portReader == null ){
			this.portReader = new MidiOutputPortProviderImpl();
		}
		return this.portReader;
	}
	
	public String getModuleId() {
		return MidiPlugin.MODULE_ID;
	}
}
