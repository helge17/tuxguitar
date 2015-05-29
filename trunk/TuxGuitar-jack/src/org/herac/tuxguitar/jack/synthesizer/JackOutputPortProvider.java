package org.herac.tuxguitar.jack.synthesizer;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.jack.provider.JackClientProvider;
import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.util.TGContext;

public class JackOutputPortProvider implements MidiOutputPortProvider{
	
	private TGContext context;
	private List<MidiOutputPort> jackOutputPorts;
	private JackClientProvider jackClientProvider;
	
	public JackOutputPortProvider(TGContext context, JackClientProvider jackClientProvider){
		this.context = context;
		this.jackClientProvider = jackClientProvider;
	}
	
	public List<MidiOutputPort> listPorts() {
		if( this.jackOutputPorts == null ){
			this.jackOutputPorts = new ArrayList<MidiOutputPort>();
			
			JackClient jackClient = this.jackClientProvider.getJackClient();
			if( jackClient != null ){
				this.jackOutputPorts.add(new JackSynthesizerPort(this.context, jackClient));
			}
		}
		return this.jackOutputPorts;
	}
	
	public void closeAll(){
		// TODO
	}
}
