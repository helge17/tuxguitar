package org.herac.tuxguitar.player.impl.midiport.oss;

import org.herac.tuxguitar.gm.port.GMOutputPort;
import org.herac.tuxguitar.gm.port.GMReceiver;

public class MidiOutputPortImpl extends GMOutputPort{
	
	private int device;
	private String name;
	private MidiReceiverImpl receiver;
	
	public MidiOutputPortImpl(MidiSystem midiSystem,String name,int device){
		this.name = name;
		this.device = device;
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
	
	public int getDevice() {
		return this.device;
	}
	
	public String getKey(){
		return ("tuxguitar-oss_" + this.device);
	}
	
	public String getName(){
		return (this.name +" #" + this.device);
	}
}