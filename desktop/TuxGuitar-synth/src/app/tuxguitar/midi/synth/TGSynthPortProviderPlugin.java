package app.tuxguitar.midi.synth;

import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class TGSynthPortProviderPlugin extends TGMidiOutputPortProviderPlugin{

	protected MidiOutputPortProvider createProvider(TGContext context) throws TGPluginException {
		return new TGSynthPortProvider(context);
	}

	public String getModuleId(){
		return TGSynthPlugin.MODULE_ID;
	}
}
