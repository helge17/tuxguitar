package org.herac.tuxguitar.jack.sequencer;

import org.herac.tuxguitar.app.system.plugins.base.TGMidiSequencerProviderPlugin;
import org.herac.tuxguitar.jack.JackPlugin;
import org.herac.tuxguitar.jack.singleton.JackClientInstanceProvider;
import org.herac.tuxguitar.player.base.MidiSequencerProvider;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class JackSequencerProviderPlugin extends TGMidiSequencerProviderPlugin {
	
	private MidiSequencerProvider jackSequencerProvider;
	
	public JackSequencerProviderPlugin(){
		this.jackSequencerProvider = new JackSequencerProvider( new JackClientInstanceProvider() );
	}
	
	protected MidiSequencerProvider getProvider() throws TGPluginException {
		return this.jackSequencerProvider;
	}
	
	public String getModuleId() {
		return JackPlugin.MODULE_ID;
	}
}
