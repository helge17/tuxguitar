package org.herac.tuxguitar.test;

import org.herac.tuxguitar.app.util.TGMusicKeyUtils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TGTestMusicKeyUtils {
	
	@Test
	public void checkNotesNames() {
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
	
		assertEquals("A",TGMusicKeyUtils.sharpNoteShortName(69));
		assertEquals("A",TGMusicKeyUtils.sharpNoteShortName(70));
		assertEquals("B",TGMusicKeyUtils.sharpNoteShortName(71));
		assertNull(TGMusicKeyUtils.sharpNoteShortName(-1));
		assertNull(TGMusicKeyUtils.sharpNoteShortName(11));
		assertNull(TGMusicKeyUtils.sharpNoteShortName(128));
		
		assertEquals("A",TGMusicKeyUtils.flatNoteShortName(69));
		assertEquals("B",TGMusicKeyUtils.flatNoteShortName(70));
		assertEquals("B",TGMusicKeyUtils.flatNoteShortName(71));
		assertNull(TGMusicKeyUtils.flatNoteShortName(-1));
		assertNull(TGMusicKeyUtils.flatNoteShortName(11));
		assertNull(TGMusicKeyUtils.flatNoteShortName(128));
		
		assertEquals("A4",TGMusicKeyUtils.sharpNoteFullName(69));
		assertEquals("A#4",TGMusicKeyUtils.sharpNoteFullName(70));
		assertEquals("A5",TGMusicKeyUtils.sharpNoteFullName(81));
		assertNull(TGMusicKeyUtils.sharpNoteFullName(-1));
		assertNull(TGMusicKeyUtils.sharpNoteFullName(11));
		assertNull(TGMusicKeyUtils.sharpNoteFullName(128));
		
		assertEquals(3, TGMusicKeyUtils.noteOctave(59));
		assertEquals(4, TGMusicKeyUtils.noteOctave(60));
		assertEquals(4, TGMusicKeyUtils.noteOctave(71));
		assertEquals(5, TGMusicKeyUtils.noteOctave(72));
		assertEquals(0,TGMusicKeyUtils.noteOctave(-1));
		assertEquals(0,TGMusicKeyUtils.noteOctave(11));
		assertEquals(0,TGMusicKeyUtils.noteOctave(128));
		
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
	
}
