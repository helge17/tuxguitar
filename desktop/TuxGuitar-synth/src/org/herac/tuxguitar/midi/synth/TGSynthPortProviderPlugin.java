package org.herac.tuxguitar.midi.synth;

import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGSynthPortProviderPlugin extends TGMidiOutputPortProviderPlugin{
	
	protected MidiOutputPortProvider createProvider(TGContext context) throws TGPluginException {
		return new TGSynthPortProvider(context);
	}
	
	public String getModuleId(){
		return TGSynthPlugin.MODULE_ID;
	}
}
