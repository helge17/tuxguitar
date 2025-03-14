package app.tuxguitar.player.impl.midiport.winmm;

import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import app.tuxguitar.util.TGContext;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin{

	public static final String MODULE_ID = "tuxguitar-winmm";

	private MidiOutputPortProviderImpl portReader;

	protected MidiOutputPortProvider createProvider(TGContext context) {
		if(this.portReader == null){
			this.portReader = new MidiOutputPortProviderImpl();
		}
		return this.portReader;
	}

	public String getModuleId(){
		return MODULE_ID;
	}
}
