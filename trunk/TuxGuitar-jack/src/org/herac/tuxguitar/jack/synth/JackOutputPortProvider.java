package org.herac.tuxguitar.jack.synth;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.jack.provider.JackClientProvider;
import org.herac.tuxguitar.jack.provider.JackSettingsProvider;
import org.herac.tuxguitar.jack.settings.JackSettings;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;

public class JackOutputPortProvider implements MidiOutputPortProvider{
	
	private List jackOutputPorts;
	private JackClientProvider jackClientProvider;
	private JackSettingsProvider jackSettingsProvider;
	
	public JackOutputPortProvider(JackClientProvider jackClientProvider, JackSettingsProvider jackSettingsProvider){
		this.jackClientProvider = jackClientProvider;
		this.jackSettingsProvider = jackSettingsProvider;
	}
	
	public List listPorts() {
		if( this.jackOutputPorts == null ){
			this.jackOutputPorts = new ArrayList();
			
			JackClient jackClient = this.jackClientProvider.getJackClient();
			JackSettings jackSettings = this.jackSettingsProvider.getJackSettings();
			if( jackClient != null && jackSettings != null ){
				this.jackOutputPorts.add(new JackSynthesizerPort(jackClient));
			}
		}
		return this.jackOutputPorts;
	}
	
	public void closeAll(){
		// TODO
	}
}
