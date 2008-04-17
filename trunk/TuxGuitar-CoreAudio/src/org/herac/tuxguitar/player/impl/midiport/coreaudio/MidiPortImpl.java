package org.herac.tuxguitar.player.impl.midiport.coreaudio;

import org.herac.tuxguitar.player.base.MidiOut;
import org.herac.tuxguitar.player.base.MidiPort;
import org.herac.tuxguitar.player.impl.midiport.coreaudio.MidiOutImpl;

public class MidiPortImpl extends MidiPort{

	//private final int port;
	//private final int client;
	private final MidiOutImpl midiOut;

	public MidiPortImpl(MidiOutImpl midiOut,String name,String key){
        super(key,name);
		//this.client = client;
		//this.port = port;
		this.midiOut = midiOut;		
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
	
    /*
	public static String toString(int client,int port){
		return (Integer.toString(client) + "-" + Integer.toString(port));
	}
     */
	
}