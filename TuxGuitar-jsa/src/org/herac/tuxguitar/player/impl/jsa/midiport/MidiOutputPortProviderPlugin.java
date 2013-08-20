package org.herac.tuxguitar.player.impl.jsa.midiport;

import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.impl.jsa.MidiPlugin;
import org.herac.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin{
	
	protected MidiOutputPortProvider getProvider() {
		return new MidiPortProviderImpl();
	}
	
	public String getModuleId() {
		return MidiPlugin.MODULE_ID;
	}
}
