package org.herac.tuxguitar.player.impl.midiport.alsa;

import org.herac.tuxguitar.player.base.MidiOut;
import org.herac.tuxguitar.player.base.MidiPort;
import org.herac.tuxguitar.player.impl.midiport.alsa.MidiOutImpl;

public class MidiPortImpl extends MidiPort{

	private final int port;
	private final int client;
	private final MidiOutImpl midiOut;

	public MidiPortImpl(MidiSystem midiSystem,String name,int client,int port){
		super(MidiPortImpl.getUniqueKey(client, port),MidiPortImpl.getUniqueName(name, client, port));
		this.client = client;
		this.port = port;
		this.midiOut = new MidiOutImpl(this,midiSystem);
	}
	
	public void open(){
		if(!this.midiOut.isConnected()){
			this.midiOut.connect();
		}
	}
	
	public void close(){
		this.midiOut.disconnect();
	}
	
	public MidiOut out(){
		this.open();
		return this.midiOut;
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