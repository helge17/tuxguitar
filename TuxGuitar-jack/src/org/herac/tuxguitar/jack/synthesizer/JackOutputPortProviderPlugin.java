package org.herac.tuxguitar.jack.synthesizer;

import org.herac.tuxguitar.gui.system.plugins.base.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;

public class JackOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin {
	
	private JackOutputPortProvider jackOutputPortProvider;
	
	public JackOutputPortProviderPlugin(){
		this(new JackClient());
	}
	
	public JackOutputPortProviderPlugin(JackClient jackClient){
		this.jackOutputPortProvider = new JackOutputPortProvider( jackClient );
	}
	
	protected MidiOutputPortProvider getProvider() {
		return this.jackOutputPortProvider;
	}
}
