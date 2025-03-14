package app.tuxguitar.jack.synthesizer;

import app.tuxguitar.jack.JackPlugin;
import app.tuxguitar.jack.singleton.JackClientInstanceProvider;
import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import app.tuxguitar.util.TGContext;

public class JackOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin {

	private JackOutputPortProvider jackOutputPortProvider;

	public JackOutputPortProviderPlugin(){
		super();
	}

	protected MidiOutputPortProvider createProvider(TGContext context) {
		if( this.jackOutputPortProvider == null ) {
			this.jackOutputPortProvider = new JackOutputPortProvider(context, new JackClientInstanceProvider(context) );
		}
		return this.jackOutputPortProvider;
	}

	public String getModuleId() {
		return JackPlugin.MODULE_ID;
	}
}
