package org.herac.tuxguitar.jack.sequencer;

import org.herac.tuxguitar.jack.JackPlugin;
import org.herac.tuxguitar.jack.singleton.JackClientInstanceProvider;
import org.herac.tuxguitar.player.base.MidiSequencerProvider;
import org.herac.tuxguitar.player.plugin.TGMidiSequencerProviderPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

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
