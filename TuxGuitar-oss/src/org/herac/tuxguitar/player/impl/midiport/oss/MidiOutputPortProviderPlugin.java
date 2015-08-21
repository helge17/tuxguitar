package org.herac.tuxguitar.player.impl.midiport.oss;

import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.util.TGContext;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin {
	
	private MidiOutputPortProviderImpl portReader;
	
	public MidiOutputPortProvider createProvider(TGContext context) {
		if( this.portReader == null ){
			this.portReader = new MidiOutputPortProviderImpl(context);
		}
		return this.portReader;
	}
	
	public String getModuleId() {
		return MidiPlugin.MODULE_ID;
	}
}
