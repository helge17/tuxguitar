package org.herac.tuxguitar.player.impl.midiport.oss;

import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin {
	
	private MidiOutputPortProviderImpl portReader;
	
	public MidiOutputPortProvider getProvider() {
		if( this.portReader == null ){
			this.portReader = new MidiOutputPortProviderImpl(getContext());
		}
		return this.portReader;
	}
	
	public String getModuleId() {
		return MidiPlugin.MODULE_ID;
	}
}
