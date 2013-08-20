package org.herac.tuxguitar.player.impl.midiport.fluidsynth;

import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin {
	
	private MidiOutputPortProviderImpl provider;
	
	protected MidiOutputPortProvider getProvider() {
		return getProviderImpl();
	}
	
	public String getModuleId() {
		return MidiSynthPlugin.MODULE_ID;
	}
	
	public MidiOutputPortProviderImpl getProviderImpl() {
		if(this.provider == null){
			this.provider = new MidiOutputPortProviderImpl();
		}
		return this.provider;
	}
}
