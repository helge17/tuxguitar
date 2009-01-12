package org.herac.tuxguitar.player.impl.midiport.fluidsynth;

import java.util.ArrayList;
import java.util.List;

public class MidiSynth {
	
	private static final String JNI_LIBRARY_NAME = new String("tuxguitar-fluidsynth-jni");
	
	static{
		System.loadLibrary(JNI_LIBRARY_NAME);
	}
	
	private long instance;
	private List drivers;
	private MidiOutputPortImpl loadedPort;
	
	public MidiSynth(){
		this.instance = malloc();
		this.drivers = new ArrayList();
		this.loadedPort = null;
	}
	
	public boolean isInitialized(){
		return (this.instance != 0);
	}
	
	public void finalize(){
		if(isInitialized()){
			this.free(this.instance);
			this.instance = 0;
		}
	}
	
	public List getDrivers(){
		this.drivers.clear();
		if(isInitialized()){
			this.findDrivers(this.instance);
		}
		return this.drivers;
	}
	
	public void loadDriver(String name){
		if(isInitialized()){
			this.loadDriver(this.instance, name);
		}
	}
	
	public boolean isConnected(MidiOutputPortImpl port){
		return (port != null && this.loadedPort != null && this.loadedPort.equals( port ) );
	}
	
	public void connect(MidiOutputPortImpl port){
		if(isInitialized()){
			this.disconnect( this.loadedPort );
			this.loadFont(this.instance, port.getSoundFont());
			this.loadedPort = port;
		}
	}
	
	public void disconnect(MidiOutputPortImpl port){
		if(isInitialized() && isConnected(port)){
			this.unloadFont(this.instance);
			this.loadedPort = null;
		}
	}
	
	public void sendSystemReset() {
		if(isInitialized()){
			this.systemReset(this.instance);
		}
	}
	
	public void sendNoteOn(int channel, int key, int velocity) {
		if(isInitialized()){
			this.noteOn(this.instance,channel, key, velocity);
		}
	}
	
	public void sendNoteOff(int channel, int key, int velocity) {
		if(isInitialized()){
			this.noteOff(this.instance,channel, key, velocity);
		}
	}
	
	public void sendControlChange(int channel, int controller, int value) {
		if(isInitialized()){
			this.controlChange(this.instance,channel, controller, value);
		}
	}
	
	public void sendProgramChange(int channel, int value) {
		if(isInitialized()){
			this.programChange(this.instance,channel, value);
		}
	}
	
	public void sendPitchBend(int channel, int value) {
		if(isInitialized()){
			this.pitchBend(this.instance,channel, value);
		}
	}
	
	protected void addDriver(String name){
		this.drivers.add(name);
	}
	
	private native long malloc();
	
	private native void free(long instance);
	
	private native void findDrivers(long instance);
	
	private native void loadDriver(long instance, String driver);
	
	private native void loadFont(long instance, String path);
	
	private native void unloadFont(long instance);
	
	private native void systemReset(long instance);
	
	private native void noteOn(long instance,int channel,int note,int velocity);
	
	private native void noteOff(long instance,int channel,int note,int velocity);
	
	private native void controlChange(long instance,int channel,int control,int value);
	
	private native void programChange(long instance,int channel,int program);
	
	private native void pitchBend(long instance,int channel,int value);
	
}
