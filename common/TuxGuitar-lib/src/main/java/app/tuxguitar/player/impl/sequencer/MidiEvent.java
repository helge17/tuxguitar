package app.tuxguitar.player.impl.sequencer;

import java.nio.charset.Charset;

public class MidiEvent {

	public static final int ALL_TRACKS = -1;

	public static final int MIDI_SYSTEM_EVENT = 1;
	public static final int MIDI_EVENT_NOTEON = 2;
	public static final int MIDI_EVENT_NOTEOFF = 3;
	public static final int MIDI_EVENT_PROGRAM_CHANGE = 4;
	public static final int MIDI_EVENT_CONTROL_CHANGE = 5;
	public static final int MIDI_EVENT_PITCH_BEND = 6;
	public static final int MIDI_EVENT_TRACK_NAME = 7;

	private long tick;
	private int type;
	private int track;
	private byte[] data;

	public MidiEvent(long tick,int type,byte[] data){
		this(tick,type,ALL_TRACKS,data);
	}

	public MidiEvent(long tick,int type,int track,byte[] data){
		this.tick = tick;
		this.type = type;
		this.track = track;
		this.data = data;
	}

	public long getTick() {
		return this.tick;
	}

	public int getType() {
		return this.type;
	}

	public int getTrack() {
		return this.track;
	}

	public byte[] getData() {
		return this.data;
	}

	// a tempo change: a system event whose data are 0x51 then the microseconds per quarter, low byte first
	public boolean isTempo() {
		return (this.type == MIDI_SYSTEM_EVENT && this.data[0] == 0x51);
	}

	public int getTempoInUSQ() {
		return ((this.data[1] & 0xff) | ((this.data[2] & 0xff) << 8) | ((this.data[3] & 0xff) << 16));
	}

	public static MidiEvent systemReset(long tick){
		return new MidiEvent(tick,MIDI_SYSTEM_EVENT,ALL_TRACKS,null);
	}

	public static MidiEvent noteOn(long tick,int track,int channel,int key,int velocity,int voice,boolean bendMode){
		return new MidiEvent(tick,MIDI_EVENT_NOTEON,track,new byte[]{(byte)channel,(byte)key,(byte)velocity,(byte)voice,(byte)(bendMode?1:0)});
	}

	public static MidiEvent noteOff(long tick,int track,int channel,int key,int velocity,int voice,boolean bendMode){
		return new MidiEvent(tick,MIDI_EVENT_NOTEOFF,track,new byte[]{(byte)channel,(byte)key,(byte)velocity,(byte)voice,(byte)(bendMode?1:0)});
	}

	public static MidiEvent pitchBend(long tick,int track,int channel,int value,int voice,boolean bendMode){
		return new MidiEvent(tick,MIDI_EVENT_PITCH_BEND,track,new byte[]{(byte)channel,(byte)value,(byte)voice,(byte)(bendMode?1:0)});
	}

	public static MidiEvent controlChange(long tick,int track,int channel,int controller,int value){
		return new MidiEvent(tick,MIDI_EVENT_CONTROL_CHANGE,track,new byte[]{(byte)channel,(byte)controller,(byte)value});
	}

	public static MidiEvent programChange(long tick,int track,int channel,int value){
		return new MidiEvent(tick,MIDI_EVENT_PROGRAM_CHANGE,track,new byte[]{(byte)channel,(byte)value});
	}

	public static MidiEvent trackName(long tick, int track, String name){
		return new MidiEvent(tick, MIDI_EVENT_TRACK_NAME, track, name.getBytes(Charset.forName("UTF-8")));
	}

	public static MidiEvent tempoInUSQ(long tick,int usq){
		return new MidiEvent(tick,MIDI_SYSTEM_EVENT,new byte[]{0x51,(byte) (usq & 0xff),(byte) ((usq >> 8) & 0xff),(byte) ((usq >> 16) & 0xff)});
	}
}
