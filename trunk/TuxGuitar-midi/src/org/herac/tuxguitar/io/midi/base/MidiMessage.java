package org.herac.tuxguitar.io.midi.base;

public class MidiMessage{
	
	public static final int TYPE_SHORT = 1;
	public static final int TYPE_META = 2;
	
	public static final int NOTE_OFF = 0x80;
	public static final int NOTE_ON = 0x90;
	public static final int CONTROL_CHANGE = 0xB0;
	public static final int PROGRAM_CHANGE = 0xC0;
	public static final int PITCH_BEND = 0xE0;
	public static final int SYSTEM_RESET = 0xFF;
	public static final int TEMPO_CHANGE = 0x51;
	public static final int TIME_SIGNATURE_CHANGE = 0x58;
	
	private int message;
	private int command;
	private byte[] data;
	
	public MidiMessage(int message, int command) {
		this.message = message;
		this.command = command;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public byte[] getData() {
		return this.data;
	}
	
	public int getType(){
		return this.message;
	}
	
	public int getCommand(){
		return this.command;
	}
	
	public static MidiMessage shortMessage(int command, int channel, int data1, int data2){
		MidiMessage message = new MidiMessage(TYPE_SHORT,command);
		message.setData(new byte[]{ (byte)( (command & 0xF0) | (channel & 0x0F) ),(byte)data1, (byte)data2  });
		return message;
	}
	
	public static MidiMessage shortMessage(int command, int channel, int data){
		MidiMessage message = new MidiMessage(TYPE_SHORT,command);
		message.setData(new byte[]{ (byte)( (command & 0xF0) | (channel & 0x0F) ),(byte)data });
		return message;
	}
	
	public static MidiMessage shortMessage(int command){
		MidiMessage message = new MidiMessage(TYPE_SHORT,command);
		message.setData(new byte[]{ (byte)command  });
		return message;
	}
	
	public static MidiMessage metaMessage(int command, byte[] data){
		MidiMessage message = new MidiMessage(TYPE_META,command);
		message.setData(data);
		return message;
	}
}
