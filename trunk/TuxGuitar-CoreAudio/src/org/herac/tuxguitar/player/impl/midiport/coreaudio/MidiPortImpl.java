package org.herac.tuxguitar.player.impl.midiport.coreaudio;

import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiReceiver;

public class MidiPortImpl extends MidiOutputPort{
	
	private final MidiReceiverImpl receiver;

	public MidiPortImpl(MidiReceiverImpl midiOut,String name,String key){
        super(key,name);
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
	
	public MidiReceiver getReceiver(){
		this.open();
		return this.receiver;
	}
	
	public void check(){
		// Not implemented
	}
}