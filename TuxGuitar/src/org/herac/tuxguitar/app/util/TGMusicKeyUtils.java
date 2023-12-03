package org.herac.tuxguitar.app.util;

public class TGMusicKeyUtils {

	public static final int MIN_MIDI_NOTE = 12;		// C0
	public static final int MAX_MIDI_NOTE = 127;	// G9, 7-bits limitation

	public static final String sharpKeyNames[] = new String[] {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	public static final String flatKeyNames[] = new String[] {"C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B"};
	
	private static boolean naturalNote[] = new boolean[] {true, false, true, false, true, true, false, true, false, true, false, true};
	
	// note name, without octave, sharp alterations 
	// e.g. 70 -> "A#"
	public static String sharpNoteName(int midiNote) {
		if (midiNote < MIN_MIDI_NOTE || midiNote > MAX_MIDI_NOTE) return null;
		return sharpKeyNames[midiNote % 12];
	}
	
	// note name, without octave, flat alterations
	// e.g. 70 -> "Bb"
	public static String flatNoteName(int midiNote) {
		if (midiNote < MIN_MIDI_NOTE || midiNote > MAX_MIDI_NOTE) return null;
		return flatKeyNames[midiNote % 12];
	}

	// note name, without octave, sharp alterations omitted
	// e.g. 70 -> "A"
	public static String sharpNoteShortName(int midiNote) {
		if (midiNote < MIN_MIDI_NOTE || midiNote > MAX_MIDI_NOTE) return null;
		return sharpKeyNames[midiNote % 12].substring(0,1);
	}
	
	// note name, without octave, flat alterations omitted
	// e.g. 70 -> "B"
	public static String flatNoteShortName(int midiNote) {
		if (midiNote < MIN_MIDI_NOTE || midiNote > MAX_MIDI_NOTE) return null;
		return flatKeyNames[midiNote % 12].substring(0,1);
	}
	
	// note octave
	// e.g. 69 -> 4
	public static int noteOctave(int midiNote) {
		if (midiNote < MIN_MIDI_NOTE || midiNote > MAX_MIDI_NOTE) return 0;
		return (midiNote/12) - 1;
	}
	
	// note name, with octave, sharp alterations
	// e.g. 70 -> "A#4"
	public static String sharpNoteFullName(int midiNote) {
		if (midiNote < MIN_MIDI_NOTE || midiNote > MAX_MIDI_NOTE) return null;
		return sharpNoteName(midiNote) + String.valueOf(noteOctave(midiNote));
	}
	
	// returns true if in [A,B,C,D,E,F,G], else false (if A#/Bb, etc)
	public static boolean isNaturalNote(int midiNote) {
		return naturalNote[midiNote % 12];
	}
	
}
