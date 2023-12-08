package org.herac.tuxguitar.player.impl.midiport.winmm;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.player.base.MidiOutputPort;

public class MidiSystem{
	
	private static final String JNI_LIBRARY_NAME = new String("tuxguitar-winmm-jni");
	
	static{
		System.loadLibrary(JNI_LIBRARY_NAME);
	}
	
	private long instance;
	private List<MidiOutputPort> ports;
	private MidiOutputPortImpl connection;
	
	public MidiSystem() {
		this.instance = malloc();
		this.ports = new ArrayList<MidiOutputPort>();
		this.connection = null;
	}
	
	public void finalize(){
		if(this.instance != 0 ){
			this.free(this.instance);
			this.instance = 0;
		}
	}
	
	public List<MidiOutputPort> findPorts(){
		this.ports.clear();
		if(this.instance != 0){
			this.findPorts(this.instance);
		}
		return this.ports;
	}
	
	public void openPort(MidiOutputPortImpl port){
		if(this.instance != 0){
			this.openPort(this.instance, port.getDevice());
			this.connection = port;
		}
	}
	
	public void closePort(){
		if(this.instance != 0){
			this.closePort(this.instance);
			this.connection = null;
		}
	}
	
	public void noteOn(int channel,int note,int velocity){
		if(this.instance != 0){
			this.noteOn(this.instance, channel, note, velocity);
		}
	}
	
	public void noteOff(int channel,int note,int velocity){
		if(this.instance != 0){
			this.noteOff(this.instance, channel, note, velocity);
		}
	}
	
	public void controlChange(int channel,int control,int value){
		if(this.instance != 0){
			this.controlChange(this.instance, channel, control, value);
		}
	}
	
	public void programChange(int channel,int program){
		if(this.instance != 0){
			this.programChange(this.instance, channel, program);
		}
	}
	
	public void pitchBend(int channel,int value){
		if(this.instance != 0){
			this.pitchBend(this.instance, channel, value);
		}
	}
	
	public MidiOutputPortImpl getConnection(){
		return this.connection;
	}
	
	protected void addPort(String name,int device){
		this.ports.add(new MidiOutputPortImpl(this,name,device));
	}
	
	private native long malloc();
	
	private native void free(long instance);
	
	protected native void open(long instance,String device);
	
	protected native void close(long instance);
	
	protected native void findPorts(long instance);
	
	protected native void openPort(long instance,int device);
	
	protected native void closePort(long instance);
	
	protected native void noteOn(long instance,int channel,int note,int velocity);
	
	protected native void noteOff(long instance,int channel,int note,int velocity);
	
	protected native void controlChange(long instance,int channel,int control,int value);
	
	protected native void programChange(long instance,int channel,int program);
	
	protected native void pitchBend(long instance,int channel,int value);
	
}
