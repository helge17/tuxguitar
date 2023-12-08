package org.herac.tuxguitar.io.midi;

public interface MidiFileHeader {
	
	public static final int	HEADER_LENGTH = 6;
	
	public static final int	HEADER_MAGIC = 0x4d546864;
	
	public static final int	TRACK_MAGIC = 0x4d54726b;
}