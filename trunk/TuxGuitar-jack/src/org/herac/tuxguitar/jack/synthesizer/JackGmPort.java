package org.herac.tuxguitar.jack.synthesizer;

import org.herac.tuxguitar.gm.port.GMOutputPort;
import org.herac.tuxguitar.gm.port.GMReceiver;
import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.jack.JackPort;
import org.herac.tuxguitar.player.base.MidiPlayerException;

public class JackGmPort  extends GMOutputPort {
	
	private JackPort jackPort;
	private JackGmReceiver jackReceiver;
	
	public JackGmPort(JackClient jackClient, JackPort jackPort){
		this.jackPort = jackPort;
		this.jackReceiver = new JackGmReceiver(jackClient, jackPort);
	}
	
	public JackPort getJackPort(){
		return this.jackPort;
	}
	
	public GMReceiver getReceiver(){
		return this.jackReceiver;
	}
	
	public void open(){
		// Not implemented
	}
	
	public void close(){
		// Not implemented
	}
	
	public void check() throws MidiPlayerException {
		// Not implemented
	}
	
	public String getKey(){
		return ("tuxguitar-jack");
	}
	
	public String getName(){
		return ("Jack Midi Port");
	}
}
