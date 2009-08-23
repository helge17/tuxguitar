package org.herac.tuxguitar.jack.synthesizer;

import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiReceiver;

public class JackOutputPort extends MidiOutputPort{
	
	private JackClient jackClient;
	private JackReceiver jackReceiver;
	private JackOutputPortRouter jackOutputPortRouter;
	
	
	public JackOutputPort( JackClient jackClient ){
		super("tuxguitar-jack","Jack Midi Port");
		this.jackOutputPortRouter = new JackOutputPortRouter();
		this.jackReceiver = new JackReceiver(jackClient, this);
		this.jackClient = jackClient;
	}
	
	public void open(){
		if(!this.jackClient.isPortsOpen()){
			this.jackClient.openPorts(this.jackOutputPortRouter.getPortCount());
		}
	}
	
	public void close(){
		if(this.jackClient.isPortsOpen()){
			this.jackClient.closePorts();
		}
	}
	
	public MidiReceiver getReceiver(){
		this.open();
		return this.jackReceiver;
	}
	
	public void check(){
		// Not implemented
	}
	
	public JackOutputPortRouter getRouter(){
		return this.jackOutputPortRouter;
	}
}
