package org.herac.tuxguitar.player.impl.midiport.oss;

import org.herac.tuxguitar.player.base.MidiOut;
import org.herac.tuxguitar.player.base.MidiPort;
import org.herac.tuxguitar.player.impl.midiport.oss.MidiOutImpl;

public class MidiPortImpl extends MidiPort{
	
	private final int device;
	private final MidiOutImpl midiOut;
	
	public MidiPortImpl(MidiSystem midiSystem,String name,int device){
		super(MidiPortImpl.getUniqueKey(device),MidiPortImpl.getUniqueName(name,device));
		this.device = device;
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
	
	public int getDevice() {
		return this.device;
	}
	
	public static String toString(int device){
		return (Integer.toString(device));
	}
	
	public static String getUniqueKey(int device){
		return ("tuxguitar-oss_" + device);
	}
	
	public static String getUniqueName(String name,int device){
		return (name +" #" + device);
	}
}