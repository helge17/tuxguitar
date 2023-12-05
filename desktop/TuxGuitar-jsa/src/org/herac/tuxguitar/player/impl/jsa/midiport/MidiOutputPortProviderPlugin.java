package org.herac.tuxguitar.player.impl.jsa.midiport;

import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.impl.jsa.MidiPlugin;
import org.herac.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.util.TGContext;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin{
	
	protected MidiOutputPortProvider createProvider(TGContext context) {
		return new MidiPortProviderImpl(context);
	}
	
	public String getModuleId() {
		return MidiPlugin.MODULE_ID;
	}
}
