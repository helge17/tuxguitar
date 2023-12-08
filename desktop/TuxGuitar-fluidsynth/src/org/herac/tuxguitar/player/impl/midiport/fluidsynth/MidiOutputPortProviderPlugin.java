package org.herac.tuxguitar.player.impl.midiport.fluidsynth;

import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.util.TGContext;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin {
	
	private MidiOutputPortProviderImpl provider;
	
	protected MidiOutputPortProvider createProvider(TGContext context) {
		return getProviderImpl(context);
	}
	
	public String getModuleId() {
		return MidiSynthPlugin.MODULE_ID;
	}
	
	public MidiOutputPortProviderImpl getProviderImpl(TGContext context) {
		if( this.provider == null ){
			this.provider = new MidiOutputPortProviderImpl(context);
		}
		return this.provider;
	}
}
