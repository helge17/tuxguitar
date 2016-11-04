package org.herac.tuxguitar.android.midimaster.port;

import org.herac.tuxguitar.gm.port.GMOutputPort;
import org.herac.tuxguitar.gm.port.GMReceiver;

public class MidiOutputPortImpl extends GMOutputPort{
	
	private MidiReceiverImpl receiver;
	
	public MidiOutputPortImpl(){
		this.receiver = new MidiReceiverImpl();
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
		return ("Sonivox EAS Midi Driver");
	}
	
	public String getName(){
		return (this.getKey());
	}
}