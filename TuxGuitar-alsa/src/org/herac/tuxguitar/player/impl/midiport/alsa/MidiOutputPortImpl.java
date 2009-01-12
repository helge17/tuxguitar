package org.herac.tuxguitar.player.impl.midiport.alsa;

import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiReceiver;

public class MidiOutputPortImpl extends MidiOutputPort{
	
	private final int port;
	private final int client;
	private final MidiReceiverImpl receiver;
	
	public MidiOutputPortImpl(MidiSystem midiSystem,String name,int client,int port){
		super(MidiOutputPortImpl.getUniqueKey(client, port),MidiOutputPortImpl.getUniqueName(name, client, port));
		this.client = client;
		this.port = port;
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
	
	public MidiReceiver getReceiver(){
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
	
	public static String getUniqueKey(int client,int port){
		return ("tuxguitar-alsa_" + client + "-" + port);
	}
	
	public static String getUniqueName(String name,int client,int port){
		return (name +" [" + client + ":" + port + "]");
	}
}