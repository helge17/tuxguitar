package org.herac.tuxguitar.player.impl.midiport.fluidsynth;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.player.impl.midiport.fluidsynth.type.BooleanRef;
import org.herac.tuxguitar.player.impl.midiport.fluidsynth.type.DoubleRef;
import org.herac.tuxguitar.player.impl.midiport.fluidsynth.type.IntegerRef;
import org.herac.tuxguitar.player.impl.midiport.fluidsynth.type.StringRef;

public class MidiSynth {
	
	private static final String JNI_LIBRARY_NAME = new String("tuxguitar-fluidsynth-jni");
	
	static{
		System.loadLibrary(JNI_LIBRARY_NAME);
	}
	
	private long instance;
	private MidiOutputPortImpl loadedPort;
	
	public MidiSynth(){
		this.instance = malloc();
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
	
	public boolean isConnected(MidiOutputPortImpl port){
		return (port != null && this.loadedPort != null && this.loadedPort.equals( port ) );
	}
	
	public void connect(MidiOutputPortImpl port){
		if(isInitialized()){
			this.disconnect( this.loadedPort );
			this.open(this.instance);
			this.loadFont(this.instance, port.getSoundFont());
			this.loadedPort = port;
		}
	}
	
	public void disconnect(MidiOutputPortImpl port){
		if(isInitialized() && isConnected(port)){
			this.unloadFont(this.instance);
			this.close(this.instance);
			this.loadedPort = null;
		}
	}
	
	public void reconnect(){
		MidiOutputPortImpl connection = this.loadedPort;
		if( isConnected( connection ) ){
			this.disconnect( connection );
			this.connect( connection );
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
	
	public void setDoubleProperty( String key , double value ){
		if(isInitialized()){
			this.setDoubleProperty(this.instance, key, value);
		}
	}
	
	public void setIntegerProperty( String key , int value ){
		if(isInitialized()){
			this.setIntegerProperty(this.instance, key, value);
		}
	}
	
	public void setStringProperty( String key , String value ){
		if(isInitialized()){
			this.setStringProperty(this.instance, key, value);
		}
	}
	
	public double getDoubleProperty( String key ){
		DoubleRef value = new DoubleRef();
		if(isInitialized()){
			this.getDoubleProperty(this.instance, key, value);
		}
		return value.getValue();
	}
	
	public int getIntegerProperty( String key ){
		IntegerRef value = new IntegerRef();
		if(isInitialized()){
			this.getIntegerProperty(this.instance, key, value);
		}
		return value.getValue();
	}
	
	public String getStringProperty( String key ){
		StringRef value = new StringRef();
		if(isInitialized()){
			this.getStringProperty(this.instance, key, value);
		}
		return value.getValue();
	}
	
	public double getDoublePropertyDefault( String key ){
		DoubleRef value = new DoubleRef();
		if(isInitialized()){
			this.getDoublePropertyDefault(this.instance, key, value);
		}
		return value.getValue();
	}
	
	public int getIntegerPropertyDefault( String key ){
		IntegerRef value = new IntegerRef();
		if(isInitialized()){
			this.getIntegerPropertyDefault(this.instance, key, value);
		}
		return value.getValue();
	}
	
	public String getStringPropertyDefault( String key ){
		StringRef value = new StringRef();
		if(isInitialized()){
			this.getStringPropertyDefault(this.instance, key, value);
		}
		return value.getValue();
	}
	
	public List getPropertyOptions( String key ){
		List options = new ArrayList();
		if(isInitialized()){
			this.getPropertyOptions(instance, key, options);
		}
		return options;
	}
	
	public int[] getIntegerPropertyRange( String key ){
		IntegerRef minimum = new IntegerRef();
		IntegerRef maximum = new IntegerRef();
		if(isInitialized()){
			this.getIntegerPropertyRange(this.instance, key, minimum , maximum );
		}
		return new int[]{ minimum.getValue() , maximum.getValue() };
	}
	
	public double[] getDoublePropertyRange( String key ){
		DoubleRef minimum = new DoubleRef();
		DoubleRef maximum = new DoubleRef();
		if(isInitialized()){
			this.getDoublePropertyRange(this.instance, key, minimum , maximum );
		}
		return new double[]{ minimum.getValue() , maximum.getValue() };
	}
	
	public boolean isRealtimeProperty( String key ){
		BooleanRef value = new BooleanRef();
		if(isInitialized()){
			this.isRealtimeProperty(this.instance, key, value);
		}
		return value.getValue();
	}
	
	private native long malloc();
	
	private native void free(long instance);
	
	private native void open(long instance);
	
	private native void close(long instance);
	
	private native void loadFont(long instance, String path);
	
	private native void unloadFont(long instance);
	
	private native void systemReset(long instance);
	
	private native void noteOn(long instance,int channel,int note,int velocity);
	
	private native void noteOff(long instance,int channel,int note,int velocity);
	
	private native void controlChange(long instance,int channel,int control,int value);
	
	private native void programChange(long instance,int channel,int program);
	
	private native void pitchBend(long instance,int channel,int value);
	
	private native void setDoubleProperty(long instance, String key , double value );
	
	private native void setIntegerProperty(long instance, String key , int value );
	
	private native void setStringProperty(long instance, String key , String value );
	
	private native void getDoubleProperty(long instance, String key , DoubleRef ref );
	
	private native void getIntegerProperty(long instance, String key , IntegerRef ref );
	
	private native void getStringProperty(long instance, String key , StringRef ref );
	
	private native void getDoublePropertyDefault(long instance, String key , DoubleRef ref );
	
	private native void getIntegerPropertyDefault(long instance, String key , IntegerRef ref );
	
	private native void getStringPropertyDefault(long instance, String key , StringRef ref );
	
	private native void getDoublePropertyRange(long instance, String key , DoubleRef minimum , DoubleRef maximum );
	
	private native void getIntegerPropertyRange(long instance, String key , IntegerRef minimum , IntegerRef maximum );
	
	private native void getPropertyOptions(long instance, String key , List options );
	
	private native void isRealtimeProperty(long instance, String key , BooleanRef ref );
}
