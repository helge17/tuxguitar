package org.herac.tuxguitar.jack.synthesizer;

import org.herac.tuxguitar.jack.JackPlugin;
import org.herac.tuxguitar.jack.singleton.JackClientInstanceProvider;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.util.TGContext;

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
