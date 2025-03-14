package app.tuxguitar.player.impl.midiport.fluidsynth;

import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import app.tuxguitar.util.TGContext;

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
