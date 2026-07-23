package app.tuxguitar.song.models;

import java.util.Arrays;
import java.util.List;

import app.tuxguitar.util.TGMusicKeyUtils;

public class TGClef {

	// the 4 base clef drawings
	// DO NOT CHANGE THESE INDEXES, they are hard-coded in many places in the code
	// (typically legacy/external file formats importers and exporters)
	public static final int INDEX_CLEF_TREBLE = 1;
	public static final int INDEX_CLEF_BASS = 2;
	public static final int INDEX_CLEF_TENOR = 3;
	public static final int INDEX_CLEF_ALTO = 4;

	// all the available clefs
	// treble guitar = 1 octave below the standard treble clef (standard = e.g. piano right hand)
	public static final TGClef CLEF_TREBLE_8 = new TGClef("composition.clef.treble-8", 65,
			new int[] { 1 , 4, 0, 3, 6, 2 , 5 }, new int[] { 5, 2, 6, 3, 7, 4, 8 }, INDEX_CLEF_TREBLE,
			"clefTreble_8.png", -1);
	// bass guitar = 1 octave below the standard bass clef (standard = e.g. piano left hand)
	public static final TGClef CLEF_BASS_8 = new TGClef("composition.clef.bass-8", 45,
			new int[] { 3 , 6, 2, 5, 8, 4 , 7 }, new int[] { 7, 4, 8, 5, 9, 6, 10 }, INDEX_CLEF_BASS,
			"clefBass_8.png", -1);
	public static final TGClef CLEF_TREBLE_STANDARD = new TGClef("composition.clef.treble-standard", 77,
			new int[] { 1 , 4, 0, 3, 6, 2 , 5 }, new int[] { 5, 2, 6, 3, 7, 4, 8 }, INDEX_CLEF_TREBLE,
			"clefTreble.png", 0);
	public static final TGClef CLEF_BASS_STANDARD = new TGClef("composition.clef.bass-standard", 57,
			new int[] { 3 , 6, 2, 5, 8, 4 , 7 }, new int[] { 7, 4, 8, 5, 9, 6, 10 }, INDEX_CLEF_BASS,
			"clefBass.png", 0);
	public static final TGClef CLEF_TENOR_8 = new TGClef("composition.clef.tenor-8", 52,
			new int[] { 7 , 3, 6, 2, 5, 1 , 4 }, new int[] { 4, 1, 5, 2, 6, 3, 7 }, INDEX_CLEF_TENOR,
			"clefTenor_8.png", -1);
	public static final TGClef CLEF_ALTO_8 = new TGClef("composition.clef.alto-8", 55,
			new int[] { 2 , 5, 1, 4, 7, 3 , 6 }, new int[] { 6, 3, 7, 4, 8, 5, 9 }, INDEX_CLEF_ALTO,
			"clefAlto_8.png", -1);
	public static final TGClef CLEF_TENOR_STANDARD = new TGClef("composition.clef.tenor-standard", 64,
			new int[] { 7 , 3, 6, 2, 5, 1 , 4 }, new int[] { 4, 1, 5, 2, 6, 3, 7 }, INDEX_CLEF_TENOR,
			"clefTenor.png", 0);
	public static final TGClef CLEF_ALTO_STANDARD = new TGClef("composition.clef.alto-standard", 67,
			new int[] { 2 , 5, 1, 4, 7, 3 , 6 }, new int[] { 6, 3, 7, 4, 8, 5, 9 }, INDEX_CLEF_ALTO,
			"clefAlto.png", 0);

	public static final TGClef DEFAULT_CLEF = CLEF_TREBLE_8;

	public static final List<TGClef> CLEFS = Arrays.asList(
			CLEF_TREBLE_8,
			CLEF_BASS_8,
			CLEF_TREBLE_STANDARD,
			CLEF_BASS_STANDARD,
			CLEF_TENOR_8,
			CLEF_ALTO_8,
			CLEF_TENOR_STANDARD,
			CLEF_ALTO_STANDARD);

	private String name;
	private int firstLineNoteIndex;		// index  of note on highest line of score, 0 = C, 1 = D, ...
	private int firstLineNoteOctave;	// octave of note on highest line of score (A in octave 4 = 440Hz)
	private int[] sharpPositions;
	private int[] flatPositions;
	private int baseClefIndex;		// for backwards compatibility (TG versions handling only the 4 base clefs)
	private String iconFileName;
	private int octaveShift;	// if -1 then a "8" character is drawn under the clef
	
	// constructor remains private, only a limited set of clefs are handled
	private TGClef(String name, int firstLineMidiNote, int[] sharpPositions, int[] flatPositions, int baseClefIndex, String iconFileName, int octaveShift) {
		this.name = name;
		this.firstLineNoteIndex = TGMusicKeyUtils.noteIndex(firstLineMidiNote, 0);
		this.firstLineNoteOctave = TGMusicKeyUtils.noteOctave(firstLineMidiNote);
		this.sharpPositions = sharpPositions;
		this.flatPositions = flatPositions;
		this.baseClefIndex = baseClefIndex;
		this.iconFileName = iconFileName;
		this.octaveShift = octaveShift;
	}

	public String getName() {
		return this.name;
	}

	public int getFirstLineNoteIndex() {
		return this.firstLineNoteIndex;
	}

	public int getFirstLineNoteOctave() {
		return this.firstLineNoteOctave;
	}

	public int getSharpPosition(int sharpIndex) {
		return this.sharpPositions[sharpIndex];
	}

	public int getFlatPosition(int flatIndex) {
		return this.flatPositions[flatIndex];
	}

	public String getIconFileName() {
		return this.iconFileName;
	}

	public int getOctaveShift() {
		return this.octaveShift;
	}

	public int getBaseClefIndex() {
		return this.baseClefIndex;
	}

}
