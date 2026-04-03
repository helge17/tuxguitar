package app.tuxguitar.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGScale;

public class TestMusicKeyUtils {

	@Test
	public void testNotesNames() {
		assertEquals("A",TGMusicKeyUtils.sharpNoteName(69));
		assertEquals("A#",TGMusicKeyUtils.sharpNoteName(70));
		assertEquals("A",TGMusicKeyUtils.sharpNoteName(81));
		assertNull(TGMusicKeyUtils.sharpNoteName(-1));
		assertNull(TGMusicKeyUtils.sharpNoteName(11));
		assertNull(TGMusicKeyUtils.sharpNoteName(128));

		assertEquals("A",TGMusicKeyUtils.flatNoteName(69));
		assertEquals("Bb",TGMusicKeyUtils.flatNoteName(70));
		assertEquals("A",TGMusicKeyUtils.flatNoteName(81));
		assertNull(TGMusicKeyUtils.flatNoteName(-1));
		assertNull(TGMusicKeyUtils.flatNoteName(11));
		assertNull(TGMusicKeyUtils.flatNoteName(128));

		assertEquals("A",TGMusicKeyUtils.noteShortName(69,0));
		assertEquals("A",TGMusicKeyUtils.noteShortName(70,1));
		assertEquals("B",TGMusicKeyUtils.noteShortName(71,2));
		assertNull(TGMusicKeyUtils.noteShortName(-1,3));
		assertNull(TGMusicKeyUtils.noteShortName(11,4));
		assertNull(TGMusicKeyUtils.noteShortName(128,5));
		assertEquals("F",TGMusicKeyUtils.noteShortName(77,5));	// F
		assertEquals("E",TGMusicKeyUtils.noteShortName(77,6));	// E#
		assertEquals("E",TGMusicKeyUtils.noteShortName(77,7));	// E#
		assertEquals("C",TGMusicKeyUtils.noteShortName(60,6));	// C
		assertEquals("B",TGMusicKeyUtils.noteShortName(60,7));	// B#

		assertEquals("A",TGMusicKeyUtils.noteShortName(69,8));
		assertEquals("B",TGMusicKeyUtils.noteShortName(70,9));
		assertEquals("B",TGMusicKeyUtils.noteShortName(71,10));
		assertNull(TGMusicKeyUtils.noteShortName(-1,11));
		assertNull(TGMusicKeyUtils.noteShortName(11,12));
		assertNull(TGMusicKeyUtils.noteShortName(128,13));
		assertEquals("B",TGMusicKeyUtils.noteShortName(59,12));	// B
		assertEquals("C",TGMusicKeyUtils.noteShortName(59,13));	// Cb
		assertEquals("C",TGMusicKeyUtils.noteShortName(59,14));	// Cb
		assertEquals("E",TGMusicKeyUtils.noteShortName(64,13));	//E
		assertEquals("F",TGMusicKeyUtils.noteShortName(64,14));	//Fb

		assertEquals("A4",TGMusicKeyUtils.sharpNoteFullName(69));
		assertEquals("A#4",TGMusicKeyUtils.sharpNoteFullName(70));
		assertEquals("A5",TGMusicKeyUtils.sharpNoteFullName(81));
		assertNull(TGMusicKeyUtils.sharpNoteFullName(-1));
		assertNull(TGMusicKeyUtils.sharpNoteFullName(11));
		assertNull(TGMusicKeyUtils.sharpNoteFullName(128));

		assertEquals("C4",TGMusicKeyUtils.noteFullName(60,6));
		assertEquals("B#3",TGMusicKeyUtils.noteFullName(60,7));
		assertEquals("E4",TGMusicKeyUtils.noteFullName(64,13));
		assertEquals("Fb4",TGMusicKeyUtils.noteFullName(64,14));
		assertEquals("Cb4",TGMusicKeyUtils.noteFullName(59,13));
		assertEquals("B3",TGMusicKeyUtils.noteFullName(59,12));

	}

	@Test
	public void testNotesOctave() {
		assertEquals(3, TGMusicKeyUtils.noteOctave(59));
		assertEquals(4, TGMusicKeyUtils.noteOctave(60));
		assertEquals(4, TGMusicKeyUtils.noteOctave(71));
		assertEquals(5, TGMusicKeyUtils.noteOctave(72));
		assertEquals(0,TGMusicKeyUtils.noteOctave(-1));
		assertEquals(0,TGMusicKeyUtils.noteOctave(11));
		assertEquals(0,TGMusicKeyUtils.noteOctave(128));

		// C4==B#3
		assertEquals(4, TGMusicKeyUtils.noteOctave(60,6));
		assertEquals(3, TGMusicKeyUtils.noteOctave(60,7));
		assertEquals(4, TGMusicKeyUtils.noteOctave(60,8));
		// B3==Cb4
		assertEquals(3, TGMusicKeyUtils.noteOctave(59,12));
		assertEquals(4, TGMusicKeyUtils.noteOctave(59,13));
		assertEquals(4, TGMusicKeyUtils.noteOctave(59,14));

	}

	@Test
	public void testNoteIsNatural() {
		assertTrue(TGMusicKeyUtils.isNaturalNote(60));
		assertFalse(TGMusicKeyUtils.isNaturalNote(61));
		assertTrue(TGMusicKeyUtils.isNaturalNote(62));
		assertFalse(TGMusicKeyUtils.isNaturalNote(63));
		assertTrue(TGMusicKeyUtils.isNaturalNote(64));
		assertTrue(TGMusicKeyUtils.isNaturalNote(65));
		assertFalse(TGMusicKeyUtils.isNaturalNote(66));
		assertTrue(TGMusicKeyUtils.isNaturalNote(67));
		assertFalse(TGMusicKeyUtils.isNaturalNote(68));
		assertTrue(TGMusicKeyUtils.isNaturalNote(69));
		assertFalse(TGMusicKeyUtils.isNaturalNote(70));
		assertTrue(TGMusicKeyUtils.isNaturalNote(71));
		assertTrue(TGMusicKeyUtils.isNaturalNote(72));
	}

	@Test
	public void testNotesPosition() {

		// D# or Eb?
		// zero or more sharps -> D#
		assertEquals(1, TGMusicKeyUtils.noteIndex(63,0));
		assertEquals(TGMusicKeyUtils.SHARP, TGMusicKeyUtils.noteAccidental(63,0));
		assertEquals(1, TGMusicKeyUtils.noteIndex(63,1));
		assertEquals(TGMusicKeyUtils.SHARP, TGMusicKeyUtils.noteAccidental(63,1));
		assertEquals(1, TGMusicKeyUtils.noteIndex(63,4));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(63,4));
		assertEquals(1, TGMusicKeyUtils.noteIndex(63,7));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(63,7));
		// 1 or more flats -> Eb
		assertEquals(2, TGMusicKeyUtils.noteIndex(63,8));
		assertEquals(TGMusicKeyUtils.FLAT, TGMusicKeyUtils.noteAccidental(63,8));
		assertEquals(2, TGMusicKeyUtils.noteIndex(63,9));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(63,9));
		assertEquals(2, TGMusicKeyUtils.noteIndex(63,14));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(63,14));

		// C or B#?
		// less than 7 sharps -> C
		assertEquals(0, TGMusicKeyUtils.noteIndex(60,0));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(60,0));
		assertEquals(0, TGMusicKeyUtils.noteIndex(60,1));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(60,1));
		assertEquals(0, TGMusicKeyUtils.noteIndex(60,2));
		assertEquals(TGMusicKeyUtils.NATURAL, TGMusicKeyUtils.noteAccidental(60,2));
		assertEquals(0, TGMusicKeyUtils.noteIndex(60,6));
		assertEquals(TGMusicKeyUtils.NATURAL, TGMusicKeyUtils.noteAccidental(60,6));
		// 7 sharps -> B#
		assertEquals(6, TGMusicKeyUtils.noteIndex(60,7));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(60,7));
		// 1 or more flats -> C
		assertEquals(0, TGMusicKeyUtils.noteIndex(60,8));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(60,8));
		assertEquals(0, TGMusicKeyUtils.noteIndex(60,12));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(60,12));
		assertEquals(0, TGMusicKeyUtils.noteIndex(60,13));
		assertEquals(TGMusicKeyUtils.NATURAL, TGMusicKeyUtils.noteAccidental(60,13));

		// F or E#?
		// less than 6 sharps -> F
		assertEquals(3, TGMusicKeyUtils.noteIndex(65,0));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(65,0));
		assertEquals(3, TGMusicKeyUtils.noteIndex(65,1));
		assertEquals(TGMusicKeyUtils.NATURAL, TGMusicKeyUtils.noteAccidental(65,1));
		assertEquals(3, TGMusicKeyUtils.noteIndex(65,5));
		assertEquals(TGMusicKeyUtils.NATURAL, TGMusicKeyUtils.noteAccidental(65,5));
		// 6 or 7 sharps -> E#
		assertEquals(2, TGMusicKeyUtils.noteIndex(65,6));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(65,6));
		assertEquals(2, TGMusicKeyUtils.noteIndex(65,7));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(65,7));
		// 1 or more flats -> F
		assertEquals(3, TGMusicKeyUtils.noteIndex(65,8));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(65,8));
		assertEquals(3, TGMusicKeyUtils.noteIndex(65,13));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(65,13));
		assertEquals(3, TGMusicKeyUtils.noteIndex(65,14));
		assertEquals(TGMusicKeyUtils.NATURAL, TGMusicKeyUtils.noteAccidental(65,14));

		// E or Fb?
		// less than 7 flats -> E
		assertEquals(2, TGMusicKeyUtils.noteIndex(64,0));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(64,0));
		assertEquals(2, TGMusicKeyUtils.noteIndex(64,5));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(64,5));
		assertEquals(2, TGMusicKeyUtils.noteIndex(64,6));
		assertEquals(TGMusicKeyUtils.NATURAL, TGMusicKeyUtils.noteAccidental(64,6));
		assertEquals(2, TGMusicKeyUtils.noteIndex(64,8));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(64,8));
		assertEquals(2, TGMusicKeyUtils.noteIndex(64,9));
		assertEquals(TGMusicKeyUtils.NATURAL, TGMusicKeyUtils.noteAccidental(64,9));
		assertEquals(2, TGMusicKeyUtils.noteIndex(64,13));
		assertEquals(TGMusicKeyUtils.NATURAL, TGMusicKeyUtils.noteAccidental(64,13));
		// 7 flats -> Fb
		assertEquals(3, TGMusicKeyUtils.noteIndex(64,14));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(64,14));

		// B or Cb?
		// less than 6 flats -> B
		assertEquals(6, TGMusicKeyUtils.noteIndex(59,0));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(59,0));
		assertEquals(6, TGMusicKeyUtils.noteIndex(59,6));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(59,6));
		assertEquals(6, TGMusicKeyUtils.noteIndex(59,7));
		assertEquals(TGMusicKeyUtils.NATURAL, TGMusicKeyUtils.noteAccidental(59,7));
		assertEquals(6, TGMusicKeyUtils.noteIndex(59,8));
		assertEquals(TGMusicKeyUtils.NATURAL, TGMusicKeyUtils.noteAccidental(59,8));
		assertEquals(6, TGMusicKeyUtils.noteIndex(59,12));
		assertEquals(TGMusicKeyUtils.NATURAL, TGMusicKeyUtils.noteAccidental(59,12));
		// 6 or 7 flats -> Cb
		assertEquals(0, TGMusicKeyUtils.noteIndex(59,13));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(59,13));
		assertEquals(0, TGMusicKeyUtils.noteIndex(59,14));
		assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(59,14));
	}

	@Test
	public void testNoteIndexToMidi(){
		// A4 -> 69, etc
		assertEquals(69, TGMusicKeyUtils.midiNote(5,4));
		assertEquals(60, TGMusicKeyUtils.midiNote(0,4));
		assertEquals(59, TGMusicKeyUtils.midiNote(6,3));
		assertEquals(53, TGMusicKeyUtils.midiNote(3,3));
		assertEquals(52, TGMusicKeyUtils.midiNote(2,3));
		assertEquals(50, TGMusicKeyUtils.midiNote(1,3));
		assertEquals(43, TGMusicKeyUtils.midiNote(4,2));
	}

	@Test
	public void testNoteIndexAlteration() {
		// all naturals, all sharps, all flats
		for (int i=0; i<7; i++) {
			assertEquals(TGMusicKeyUtils.NATURAL, TGMusicKeyUtils.noteIndexAlteration(i, 0));
			assertEquals(TGMusicKeyUtils.SHARP, TGMusicKeyUtils.noteIndexAlteration(i, 7));
			assertEquals(TGMusicKeyUtils.FLAT, TGMusicKeyUtils.noteIndexAlteration(i, 14));
		}
		// 1 sharp/flat
		for (int i=0; i<7; i++) {
			assertEquals((i==3 ? TGMusicKeyUtils.SHARP: TGMusicKeyUtils.NATURAL), TGMusicKeyUtils.noteIndexAlteration(i, 1));
			assertEquals((i==6 ? TGMusicKeyUtils.FLAT : TGMusicKeyUtils.NATURAL), TGMusicKeyUtils.noteIndexAlteration(i, 8));
		}
		// 6 sharp/flat
		for (int i=0; i<7; i++) {
			assertEquals((i!=6 ? TGMusicKeyUtils.SHARP: TGMusicKeyUtils.NATURAL), TGMusicKeyUtils.noteIndexAlteration(i, 6));
			assertEquals((i!=3 ? TGMusicKeyUtils.FLAT : TGMusicKeyUtils.NATURAL), TGMusicKeyUtils.noteIndexAlteration(i, 13));
		}
	}

	@Test
	public void testAlterations() {
		assertEquals(TGMusicKeyUtils.NATURAL, TGMusicKeyUtils.noteAlteration(60, 0));
		for (int key=0; key<=7; key++) {
			assertEquals(TGMusicKeyUtils.SHARP, TGMusicKeyUtils.noteAlteration(61, key));
		}
		for (int key=0; key<=3; key ++) {
			assertEquals(TGMusicKeyUtils.SHARP, TGMusicKeyUtils.noteAccidental(63, key));
		}
		for (int key=4; key<=7; key ++) {
			assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(61, key));
		}

		for (int key=8; key<=14; key++) {
			assertEquals(TGMusicKeyUtils.FLAT, TGMusicKeyUtils.noteAlteration(61, key));
		}
		for (int key=8; key<=11; key ++) {
			assertEquals(TGMusicKeyUtils.FLAT, TGMusicKeyUtils.noteAccidental(66, key));
		}
		for (int key=12; key<=14; key ++) {
			assertEquals(TGMusicKeyUtils.NONE, TGMusicKeyUtils.noteAccidental(66, key));
		}
	}

	@Test
	public void testAddInterval() {
		// notes indexes (i.e. C, D, ... B)
		assertEquals(2, TGMusicKeyUtils.noteIndexAddInterval(0, 2)); // C+2 = E
		assertEquals(0, TGMusicKeyUtils.noteIndexAddInterval(0, 7)); // C+7 = C
		assertEquals(6, TGMusicKeyUtils.noteIndexAddInterval(0, -1)); // C-1 = B
		assertEquals(5, TGMusicKeyUtils.noteIndexAddInterval(6, -8)); // C-8 = A
		// notes octaves
		assertEquals(3, TGMusicKeyUtils.noteOctaveAddInterval(0, 4, -1)); // C4-1 = octave 3
		assertEquals(5, TGMusicKeyUtils.noteOctaveAddInterval(5, 4, 2)); //  A4+2 = octave 5
		assertEquals(4, TGMusicKeyUtils.noteOctaveAddInterval(3, 4, -2)); // F4-8 = octave 4
		assertEquals(3, TGMusicKeyUtils.noteOctaveAddInterval(3, 4, -8)); // F4-8 = octave 3
	}

	// check names, alternative enharmonic representation
	@Test
	public void testAltName() {
		// A is always A, whatever enharmonic representation
		assertEquals("A",TGMusicKeyUtils.noteShortName(69,0,true));
		assertEquals("A",TGMusicKeyUtils.noteName(69,0,true));
		assertEquals("A",TGMusicKeyUtils.noteShortName(69,7,true));
		assertEquals("A",TGMusicKeyUtils.noteName(69,7,true));
		assertEquals("A",TGMusicKeyUtils.noteShortName(69,14,true));
		assertEquals("A",TGMusicKeyUtils.noteName(69,14,true));

		// alternative of A# is Bb
		assertEquals("B",TGMusicKeyUtils.noteShortName(70,0,true));
		assertEquals("Bb",TGMusicKeyUtils.noteName(70,0,true));
		assertEquals("B",TGMusicKeyUtils.noteShortName(70,4,true));
		assertEquals("Bb",TGMusicKeyUtils.noteName(70,4,true));
		// alternative of Bb is A#
		assertEquals("A",TGMusicKeyUtils.noteShortName(70,8,true));
		assertEquals("A#",TGMusicKeyUtils.noteName(70,8,true));

		// B -> Cb
		assertEquals("C",TGMusicKeyUtils.noteShortName(71,7,true));
		assertEquals("Cb",TGMusicKeyUtils.noteName(71,7,true));
		assertEquals("C",TGMusicKeyUtils.noteShortName(71,12,true));
		assertEquals("Cb",TGMusicKeyUtils.noteName(71,12,true));
		// Cb -> B
		assertEquals("B",TGMusicKeyUtils.noteShortName(71,13,true));
		assertEquals("B",TGMusicKeyUtils.noteName(71,13,true));

		// C -> B#
		assertEquals("B",TGMusicKeyUtils.noteShortName(72,6,true));
		assertEquals("B#",TGMusicKeyUtils.noteName(72,6,true));
		assertEquals("B",TGMusicKeyUtils.noteShortName(72,14,true));
		assertEquals("B#",TGMusicKeyUtils.noteName(72,14,true));
		// B# -> C
		assertEquals("C",TGMusicKeyUtils.noteShortName(72,7,true));
		assertEquals("C",TGMusicKeyUtils.noteName(72,7,true));

		// C# -> Db
		assertEquals("D",TGMusicKeyUtils.noteShortName(73,0,true));
		assertEquals("Db",TGMusicKeyUtils.noteName(73,0,true));
		assertEquals("D",TGMusicKeyUtils.noteShortName(73,7,true));
		assertEquals("Db",TGMusicKeyUtils.noteName(73,7,true));
		// Db -> C#
		assertEquals("C",TGMusicKeyUtils.noteShortName(73,8,true));
		assertEquals("C#",TGMusicKeyUtils.noteName(73,8,true));

		// D -> D
		assertEquals("D",TGMusicKeyUtils.noteShortName(74,0,true));
		assertEquals("D",TGMusicKeyUtils.noteName(74,0,true));
		assertEquals("D",TGMusicKeyUtils.noteShortName(74,7,true));
		assertEquals("D",TGMusicKeyUtils.noteName(74,7,true));
		assertEquals("D",TGMusicKeyUtils.noteShortName(74,14,true));
		assertEquals("D",TGMusicKeyUtils.noteName(74,14,true));

		// D# -> Eb
		assertEquals("E",TGMusicKeyUtils.noteShortName(75,0,true));
		assertEquals("Eb",TGMusicKeyUtils.noteName(75,0,true));
		// Eb -> D#
		assertEquals("D",TGMusicKeyUtils.noteShortName(75,8,true));
		assertEquals("D#",TGMusicKeyUtils.noteName(75,8,true));
		assertEquals("D",TGMusicKeyUtils.noteShortName(75,9,true));
		assertEquals("D#",TGMusicKeyUtils.noteName(75,9,true));

		// E -> Fb
		assertEquals("F",TGMusicKeyUtils.noteShortName(76,0,true));
		assertEquals("Fb",TGMusicKeyUtils.noteName(76,0,true));
		assertEquals("F",TGMusicKeyUtils.noteShortName(76,13,true));
		assertEquals("Fb",TGMusicKeyUtils.noteName(76,13,true));
		// Fb -> E
		assertEquals("E",TGMusicKeyUtils.noteShortName(76,14,true));
		assertEquals("E",TGMusicKeyUtils.noteName(76,14,true));

		// F -> E#
		assertEquals("E",TGMusicKeyUtils.noteShortName(77,5,true));
		assertEquals("E#",TGMusicKeyUtils.noteName(77,5,true));
		assertEquals("E",TGMusicKeyUtils.noteShortName(77,14,true));
		assertEquals("E#",TGMusicKeyUtils.noteName(77,14,true));
		// E# -> F
		assertEquals("F",TGMusicKeyUtils.noteShortName(77,6,true));
		assertEquals("F",TGMusicKeyUtils.noteName(77,6,true));

		// F# -> Gb
		assertEquals("G",TGMusicKeyUtils.noteShortName(78,0,true));
		assertEquals("Gb",TGMusicKeyUtils.noteName(78,0,true));
		// Gb -> F#
		assertEquals("F",TGMusicKeyUtils.noteShortName(78,8,true));
		assertEquals("F#",TGMusicKeyUtils.noteName(78,8,true));
		assertEquals("F",TGMusicKeyUtils.noteShortName(78,12,true));
		assertEquals("F#",TGMusicKeyUtils.noteName(78,12,true));

		// G -> G
		assertEquals("G",TGMusicKeyUtils.noteShortName(79,0,true));
		assertEquals("G",TGMusicKeyUtils.noteName(79,0,true));
		assertEquals("G",TGMusicKeyUtils.noteShortName(79,7,true));
		assertEquals("G",TGMusicKeyUtils.noteName(79,7,true));
		assertEquals("G",TGMusicKeyUtils.noteShortName(79,14,true));
		assertEquals("G",TGMusicKeyUtils.noteName(79,14,true));

		// G# -> Ab
		assertEquals("A",TGMusicKeyUtils.noteShortName(80,0,true));
		assertEquals("Ab",TGMusicKeyUtils.noteName(80,0,true));
		// Ab -> G#
		assertEquals("G",TGMusicKeyUtils.noteShortName(80,8,true));
		assertEquals("G#",TGMusicKeyUtils.noteName(80,8,true));
		assertEquals("G",TGMusicKeyUtils.noteShortName(80,10,true));
		assertEquals("G#",TGMusicKeyUtils.noteName(80,10,true));
	}
	@Test
	public void testAltOctave() {
		// C4==B#3
		assertEquals(3, TGMusicKeyUtils.noteOctave(60,6,true));
		assertEquals(4, TGMusicKeyUtils.noteOctave(60,7,true));
		assertEquals(3, TGMusicKeyUtils.noteOctave(60,8,true));
		// B3==Cb4
		assertEquals(4, TGMusicKeyUtils.noteOctave(59,12,true));
		assertEquals(3, TGMusicKeyUtils.noteOctave(59,13,true));
		assertEquals(3, TGMusicKeyUtils.noteOctave(59,14,true));

	}

	@Test
	public void testAltPosition() {
		for (int keySignature=0; keySignature<=14; keySignature++) {
			for (int midiNote=69; midiNote<81; midiNote++) {
				if ((midiNote==69) || (midiNote==74) || (midiNote==79)) {
					// A, D, G -> alternative enharmonic representation equals normal representation
					assertEquals(TGMusicKeyUtils.noteIndex(midiNote,keySignature), TGMusicKeyUtils.noteIndex(midiNote,keySignature,true));
					assertEquals(TGMusicKeyUtils.noteAccidental(midiNote,keySignature), TGMusicKeyUtils.noteAccidental(midiNote,keySignature,true));
				}
				else {
					// alternative shall differ from normal
					assertNotEquals(TGMusicKeyUtils.noteIndex(midiNote,keySignature), TGMusicKeyUtils.noteIndex(midiNote,keySignature,true));
					assertNotEquals(TGMusicKeyUtils.noteAccidental(midiNote,keySignature), TGMusicKeyUtils.noteAccidental(midiNote,keySignature,true));
				}
			}
		}
	}

	@Test
	public void testScaleKeySignature() {
		TGFactory factory = new TGFactory();
		// major scale
		TGScale scale = factory.newScale();
		scale.setNote(0, true);
		scale.setNote(2, true);
		scale.setNote(4, true);
		scale.setNote(5, true);
		scale.setNote(7, true);
		scale.setNote(9, true);
		scale.setNote(11, true);

		scale.setKeyName("C");
		assertEquals(0, TGMusicKeyUtils.getKeySignature(scale));

		scale.setKeyName("D");
		assertEquals(2, TGMusicKeyUtils.getKeySignature(scale));

		scale.setKeyName("Eb");
		assertEquals(7 + 3, TGMusicKeyUtils.getKeySignature(scale));

		scale.setKeyName("F");
		assertEquals(7 + 1, TGMusicKeyUtils.getKeySignature(scale));

		scale.setKeyName("C#");
		assertEquals(7, TGMusicKeyUtils.getKeySignature(scale));

		// minor scale
		scale.clear();
		scale.setNote(0, true);
		scale.setNote(2, true);
		scale.setNote(3, true);
		scale.setNote(5, true);
		scale.setNote(7, true);
		scale.setNote(8, true);
		scale.setNote(10, true);

		scale.setKeyName("A");
		assertEquals(0, TGMusicKeyUtils.getKeySignature(scale));

		scale.setKeyName("C");
		assertEquals(7+3, TGMusicKeyUtils.getKeySignature(scale));

	}

}
