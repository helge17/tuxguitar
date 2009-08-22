package org.herac.tuxguitar.jack.synthesizer;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;

public class JackOutputPortProvider implements MidiOutputPortProvider{
	
	private List jackOutputPorts;
	private JackClient jackClient;
	
	public JackOutputPortProvider(JackClient jackClient){
		this.jackClient = jackClient;
	}
	
	public List listPorts() {
		if(this.jackOutputPorts == null){
			this.jackOutputPorts = new ArrayList();
			this.jackOutputPorts.add(new JackOutputPort( this.jackClient ));
		}
		return this.jackOutputPorts;
	}
	
	public void closeAll(){
		if(this.jackClient.isPortsOpen()){
			this.jackClient.closePorts();
		}
	}
	
}
