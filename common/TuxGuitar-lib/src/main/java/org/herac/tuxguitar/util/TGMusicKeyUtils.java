package org.herac.tuxguitar.util;

/*
 * This class provides generic static methods for notes manipulation:
 * - conversion from note pitch to note name, octave, alteration, accidental
 * - conversion from note name and octave to note pitch
 * - evaluation of presence of alterations, accidentals
 * - addition of interval to note
 * 
 * Conventions:
 * - note "index" is an integer in the range [0..6], 0=C, 1=D, 2=E, 3=F, 4=G, 5=A, 6=B
 * - "midiNote" corresponds to note pitch as defined by general midi, midiNote 69 = 440 Hz
 * - octave number follows general midi convention: in octave 4, A = 440Hz
 * - keySignature is encoded as everywhere in TuxGuitar: 0 = all naturals, 1 to 7 = 1 to 7 sharps, 8 to 15 = 1 to 7 flats
 * - alteration can be NATURAL, SHARP, FLAT
 *   e.g. with keySignature 2 sharps, alteration(D#/Eb) = SHARP, alteration(natural F) = NATURAL, alteration(C#/Db) = SHARP
 * - accidental can be NONE, NATURAL, SHARP, FLAT
 *   e.g. with keySignature 2 sharps, accidental(D#/Eb) = SHARP, accidental(natural F) = NATURAL, accidental(C#/Db) = NONE
 */


public class TGMusicKeyUtils {

	public static final int MIN_MIDI_NOTE = 12;		// C0
	public static final int MAX_MIDI_NOTE = 127;	// G9, 7-bits limitation

	// as far as possible do not use these constants, prefer using methods in this class
	public static final String sharpKeyNames[] = new String[] {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	public static final String flatKeyNames[] = new String[] {"C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B"};
	
	// notes indexes
	private static final int[] indexesSharp =  {0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6};
	private static final int[] indexes6Sharp = {0, 0, 1, 1, 2, 2, 3, 4, 4, 5, 5, 6};	// E#
	private static final int[] indexes7Sharp = {6, 0, 1, 1, 2, 2, 3, 4, 4, 5, 5, 6};	// E#, B#
	private static final int[] indexesFlat =   {0, 1, 1, 2, 2, 3, 4, 4, 5, 5, 6, 6};
	private static final int[] indexes6Flat =  {0, 1, 1, 2, 2, 3, 4, 4, 5, 5, 6, 0};	// Cb
	private static final int[] indexes7Flat =  {0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6, 0};	// Cb, Fb
	// which index table to consider, depending from signatureKey
	// so that tableIndex[keySignature] corresponds to the correct indexesXXX array
	// keySignature 0..5 -> indexesSharp, 6 -> indexes6Sharp, etc
	private static final int[][] tableIndex = {indexesSharp, indexes6Sharp, indexes7Sharp, indexesFlat, indexes6Flat, indexes7Flat};
	private static final int[] indexKeySignature = {0,0,0,0,0,0,1,2,3,3,3,3,3,4,5};
	// order of sharps (note indexes) FCGDAEB
	private static final int[] sharps = {3,0,4,1,5,2,6};
	// notes names per index
	private static final String[] names = {"C","D","E","F","G","A","B"};
	
	// accidentals
	public static final int NONE = 0;
	// accidentals, alterations
	public static final int NATURAL = 1;
	public static final int SHARP = 2;
	public static final int FLAT = 3;
	
	private static boolean naturalNote[] = new boolean[] {true, false, true, false, true, true, false, true, false, true, false, true};
	
	// ----- "default" note name and octave: without considering keySignature -------
	
	// midi note name, without octave, sharp alterations 
	// e.g. 70 -> "A#"
	public static String sharpNoteName(int midiNote) {
		if (midiNote < MIN_MIDI_NOTE || midiNote > MAX_MIDI_NOTE) return null;
		return sharpKeyNames[midiNote % 12];
	}
	
	// midi note name, without octave, flat alterations
	// e.g. 70 -> "Bb"
	public static String flatNoteName(int midiNote) {
		if (midiNote < MIN_MIDI_NOTE || midiNote > MAX_MIDI_NOTE) return null;
		return flatKeyNames[midiNote % 12];
	}
	
	// note octave
	// e.g. 70 -> 4
	public static int noteOctave(int midiNote) {
		return noteOctave(midiNote,0);
	}
	
	// midi note name, with octave, sharp alterations
	// e.g. 70 -> "A#4"
	public static String sharpNoteFullName(int midiNote) {
		if (midiNote < MIN_MIDI_NOTE || midiNote > MAX_MIDI_NOTE) return null;
		return sharpNoteName(midiNote) + String.valueOf(noteOctave(midiNote));
	}
	
	// returns true if midi note in [A,B,C,D,E,F,G], else false (if A#/Bb, etc)
	// don't use this method if keySignature needs to be considered (e.g. "C" could in fact be "B#")
	public static boolean isNaturalNote(int midiNote) {
		return naturalNote[midiNote % 12];
	}
	
	// ----- note name and octave: considering keySignature -------
	
	// midi note name, with octave, considering key signature
	// e.g. 70 -> "A#4" if key signature = 0 or n sharps / -> "Gb4" if key signature = 1 or n flats
	public static String noteFullName(int midiNote, int keySignature) {
		if (midiNote < MIN_MIDI_NOTE || midiNote > MAX_MIDI_NOTE) return null;
		if (keySignature<0 || keySignature>14) return null;
		return noteName(midiNote, keySignature) + String.valueOf(noteOctave(midiNote, keySignature));
	}

	// midi note name, without octave, considering key signature
	// e.g. 70 -> "A#" if key signature = 0 or n sharps / -> "Gb" if key signature = 1 or n flats
	public static String noteName(int midiNote, int keySignature) {
		if (midiNote < MIN_MIDI_NOTE || midiNote > MAX_MIDI_NOTE) return null;
		if (keySignature<0 || keySignature>14) return null;
		int alteration = noteAlteration(midiNote, keySignature);
		String alterationString = alteration==SHARP ? "#" : (alteration == FLAT ? "b" : "");
		return noteShortName(midiNote, keySignature) + alterationString;
	}

	// midi note short name, without alteration, without octave, considering key signature
	// e.g. 70 -> "A" if key signature = 0 or n sharps / -> "G" if key signature = 1 or n flats
	public static String noteShortName(int midiNote, int keySignature) {
		if (midiNote < MIN_MIDI_NOTE || midiNote > MAX_MIDI_NOTE) return null;
		if (keySignature<0 || keySignature>14) return null;
		return names[noteIndex(midiNote, keySignature)];
	}
	
	// midi note octave, considering keySignature
	// because C4 == B#3, and Cb4 == B3
	public static int noteOctave(int midiNote, int keySignature) {
		if (midiNote < MIN_MIDI_NOTE || midiNote > MAX_MIDI_NOTE) return 0;
		if (keySignature < 0 || keySignature > 14) return 0;
		
		int octave = (midiNote/12) - 1;
		// B#?
		if (keySignature == 7 && (midiNote % 12) == 0 ) {
			octave --;
		}
		// Cb?
		else if (keySignature>=13 && (midiNote % 12) == 11 ) {
			octave++;
		}
		return octave;
	}
	
	// midi note index, considering keySignature
	public static int noteIndex(int midiNote, int keySignature) {
		if (keySignature<0 || keySignature>14) return 0;
		return tableIndex[indexKeySignature[keySignature]][midiNote%12];
	}
	
	// midi note alteration: flat, natural, sharp (sharp of flat is deduced from keySignature)
	public static int noteAlteration(int midiNote, int keySignature) {
		int alteration = NATURAL;
		int index = noteIndex(midiNote, keySignature);
		int indexFlat = noteIndex(midiNote, 8);
		if ((7 + indexFlat - index) % 7 == 1) {
			alteration = SHARP;
		}
		else {
			int indexSharp = noteIndex(midiNote, 0);
			if ((7 + indexSharp - index) %7 == 6) {
				alteration = FLAT;
			}
		}
		return alteration;
	}
	
	// midi note accidental, considering keySignature: none, flat, natural, sharp
	// returns none if not is altered with an alteration present in keySignature
	public static int noteAccidental(int midiNote, int keySignature) {
		int alteration = noteAlteration(midiNote, keySignature);
		// compare with expected alteration considering keySignature
		int index = noteIndex(midiNote, keySignature);
		return (alteration == noteIndexAlteration(index, keySignature)) ? NONE : alteration;
	}
	
	// alteration of note index, considering key signature
	// e.g. with keySignature==2, alteration of C (index 0) -> SHARP
	public static int noteIndexAlteration(int noteIndex, int keySignature) {
		if (keySignature == 0) {
			return NATURAL;
		}
		if (keySignature <= 7) {
			for (int i=1; i<=keySignature; i++) {
				if (noteIndex == sharps[i-1]) {
					return(SHARP);
				}
			}
		} else {
			for (int i=8; i<=keySignature; i++) {
				if (noteIndex == sharps[14-i]) {
					return(FLAT);
				}
			}
		}
		return NATURAL;
	}
	
	// ----- note name and octave: considering keySignature and alternative enharmonic representation -------
	
	public static String noteName(int midiNote, int keySignature, boolean altEnharmonic) {
		String normalName = noteName(midiNote, keySignature);
		if (!altEnharmonic) return normalName;
		String name = noteName(midiNote, 7);
		if (!name.equals(normalName)) return name;
		return noteName(midiNote, 14);
	}
	public static String noteShortName(int midiNote, int keySignature, boolean altEnharmonic) {
		String normalShortName = noteShortName(midiNote, keySignature);
		if (!altEnharmonic) return normalShortName;
		String shortName = noteShortName(midiNote, 7);
		if (!shortName.equals(normalShortName)) return shortName;
		return noteShortName(midiNote, 14);
	}
	public static int noteOctave(int midiNote, int keySignature, boolean altEnharmonic) {
		int normalOctave = noteOctave(midiNote, keySignature);
		if (!altEnharmonic) return normalOctave;
		int octave = noteOctave(midiNote, 7);
		if (octave != normalOctave) return octave;
		return noteOctave(midiNote,14);
	}
	public static int noteIndex(int midiNote, int keySignature, boolean altEnharmonic) {
		int normalIndex = noteIndex(midiNote, keySignature);
		if (!altEnharmonic) return normalIndex;
		int index = noteIndex(midiNote, 7);
		if (index != normalIndex) return index;
		return noteIndex(midiNote, 14);
	}
	public static int noteAccidental(int midiNote, int keySignature, boolean altEnharmonic) {
		int normalAccidental = noteAccidental(midiNote, keySignature);
		if (!altEnharmonic) {
			return normalAccidental;
		}
		int accidental;
		int index = noteIndex(midiNote, keySignature);
		if (noteIndex(midiNote,7) != index) {
			accidental = noteAlteration(midiNote, 7);
			return (accidental == NONE ? SHARP : accidental);
		}
		else if (noteIndex(midiNote,14) != index) {
			accidental = noteAlteration(midiNote, 14);
			return (accidental == NONE ? FLAT : accidental);
		}
		return normalAccidental;
	}
		
		// ----- additions of offset -------
	// addition of offset to note index, ex: C-1->B, C+1->D
	public static int noteIndexAddInterval(int noteIndex, int offset) {
		int index = (noteIndex + offset) % 7;
		if (index<0) {
			index += 7;
		}
		return index;
	}
	// octave after addition of offset to note index, ex: C4-1->3, C4+1->4
	public static int noteOctaveAddInterval(int noteIndex, int octave, int offset) {
		return octave + (int)Math.floor((float)(noteIndex + offset)/7);
	}
	
	// ----- midi note from note index -------
	
	// (noteIndex=5 (A), octave=4) -> midi note 69
	public static int midiNote(int noteIndex, int octave) {
		int semiTonesToC[] = {0,2,4,5,7,9,11};
		return 12*(1+octave) + semiTonesToC[noteIndex];
	}
	
}
