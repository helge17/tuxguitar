package org.herac.tuxguitar.player.impl.midiport.alsa;

import org.herac.tuxguitar.gm.port.GMOutputPort;
import org.herac.tuxguitar.gm.port.GMReceiver;

public class MidiOutputPortImpl extends GMOutputPort{
	
	private int port;
	private int client;
	private String clientName;
	private MidiReceiverImpl receiver;
	
	public MidiOutputPortImpl(MidiSystem midiSystem,String clientName,int client,int port){
		this.port = port;
		this.client = client;
		this.clientName = clientName;
		this.receiver = new MidiReceiverImpl(this,midiSystem);
	}
	
	public void open(){
		if(!this.receiver.isConnected()){
			this.receiver.connect();
		}
	}
	
	public void close(){
		this.receiver.disconnect();
	}
	
	public GMReceiver getReceiver(){
		this.open();
		return this.receiver;
	}
	
	public void check(){
		// Not implemented
	}
	
	public int getPort() {
		return this.port;
	}
	
	public int getClient() {
		return this.client;
	}
	
	public String getKey(){
		return ("tuxguitar-alsa_" + this.client + "-" + this.port);
	}
	
	public String getName(){
		return (this.clientName +" [" + this.client + ":" + this.port + "]");
	}
}