package app.tuxguitar.jack.sequencer;

import app.tuxguitar.jack.JackPlugin;
import app.tuxguitar.jack.singleton.JackClientInstanceProvider;
import app.tuxguitar.player.base.MidiSequencerProvider;
import app.tuxguitar.player.plugin.TGMidiSequencerProviderPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class JackSequencerProviderPlugin extends TGMidiSequencerProviderPlugin {

	private MidiSequencerProvider jackSequencerProvider;

	public JackSequencerProviderPlugin(){
		super();
	}

	protected MidiSequencerProvider createProvider(TGContext context) throws TGPluginException {
		if( this.jackSequencerProvider == null ) {
			this.jackSequencerProvider = new JackSequencerProvider(context, new JackClientInstanceProvider(context) );
		}
		return this.jackSequencerProvider;
	}

	public String getModuleId() {
		return JackPlugin.MODULE_ID;
	}
}
