package org.herac.tuxguitar.jack.sequencer;

import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.gui.system.plugins.base.TGMidiSequencerProviderPlugin;
import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.player.base.MidiSequencerProvider;

public class JackSequencerProviderPlugin extends TGMidiSequencerProviderPlugin {
	
	private MidiSequencerProvider jackSequencerProvider;
	
	public JackSequencerProviderPlugin(){
		this(new JackClient());
	}
	
	public JackSequencerProviderPlugin(JackClient jackClient){
		this.jackSequencerProvider = new JackSequencerProvider( jackClient );
	}
	
	protected MidiSequencerProvider getProvider() throws TGPluginException {
		return this.jackSequencerProvider;
	}
}
