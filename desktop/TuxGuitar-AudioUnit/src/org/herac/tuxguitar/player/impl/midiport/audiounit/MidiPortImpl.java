package org.herac.tuxguitar.player.impl.midiport.audiounit;

import org.herac.tuxguitar.gm.port.GMOutputPort;
import org.herac.tuxguitar.gm.port.GMReceiver;

public class MidiPortImpl extends GMOutputPort{
	
	private String key;
	private String name;
	private MidiReceiverImpl receiver;

	public MidiPortImpl(MidiReceiverImpl midiOut,String name,String key){
        this.key = key;
        this.name = name;
		this.receiver = midiOut;		
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
	
	public String getKey(){
		return this.key;
	}
	
	public String getName(){
		return this.name;
	}
}