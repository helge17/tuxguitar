package org.herac.tuxguitar.jack.synth;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.jack.provider.JackClientProvider;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;

public class JackOutputPortProvider implements MidiOutputPortProvider{
	
	private List jackOutputPorts;
	private JackClientProvider jackClientProvider;
	
	public JackOutputPortProvider(JackClientProvider jackClientProvider){
		this.jackClientProvider = jackClientProvider;
	}
	
	public List listPorts() {
		if( this.jackOutputPorts == null ){
			this.jackOutputPorts = new ArrayList();
			
			JackClient jackClient = this.jackClientProvider.getJackClient();
			if( jackClient != null ){
				this.jackOutputPorts.add(new JackSynthesizerPort(jackClient));
			}
		}
		return this.jackOutputPorts;
	}
	
	public void closeAll(){
		// TODO
	}
}
