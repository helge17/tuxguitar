package org.herac.tuxguitar.player.impl.midiport.audiounit;

public abstract class MidiReceiverJNI{	
	
	private static final String JNI_LIBRARY_NAME = new String("tuxguitar-audiounit-jni");
	
	static{
		System.loadLibrary (JNI_LIBRARY_NAME);
	}
	
	public MidiReceiverJNI() {
		super();
	}
	
	protected native void open();
	
	protected native void close();	
	
	//protected native void findDevices();
	
	protected native void openDevice();
	
	protected native void closeDevice();
	
	protected native void noteOn(int channel,int note,int velocity);
	
	protected native void noteOff(int channel,int note,int velocity);

	protected native void controlChange(int channel,int control,int value);
	
	protected native void programChange(int channel,int program);
	
	protected native void pitchBend(int channel,int value);

	
	//protected abstract void addDevice(String name);
}
