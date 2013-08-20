package org.herac.tuxguitar.jack.synthesizer;

import org.herac.tuxguitar.jack.JackPlugin;
import org.herac.tuxguitar.jack.singleton.JackClientInstanceProvider;
import org.herac.tuxguitar.jack.singleton.JackSettingsInstanceProvider;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;

public class JackOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin {
	
	private JackOutputPortProvider jackOutputPortProvider;
	
	public JackOutputPortProviderPlugin(){
		this.jackOutputPortProvider = new JackOutputPortProvider(new JackClientInstanceProvider() , new JackSettingsInstanceProvider());
	}
	
	protected MidiOutputPortProvider getProvider() {
		return this.jackOutputPortProvider;
	}
	
	public String getModuleId() {
		return JackPlugin.MODULE_ID;
	}
}
