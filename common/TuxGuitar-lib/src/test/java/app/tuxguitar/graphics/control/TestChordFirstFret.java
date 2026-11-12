package app.tuxguitar.graphics.control;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * Pins which fret a chord diagram starts at (TGChordImpl.calculateFirstFret): the 1st when the chord has an open
 * string and fits in the 6-fret grid, else its lowest fretted note (never below the 1st); notes 5 or more frets
 * above that start do not fit the grid and are dropped (-1).
 *
 * A chord is given by its frets from the first string on (-1: not played) and read back as "first=N [frets]".
 */
public class TestChordFirstFret {

	private static String firstFret(int... frets) {
		TGChordImpl chord = new TGChordImpl(frets.length);
		for (int string = 0; string < frets.length; string++) {
			chord.addFretValue(string, frets[string]);
		}
		chord.calculateFirstFret();
		return "first=" + chord.getFirstFret() + " " + Arrays.toString(chord.getStrings());
	}

	@Test
	public void eachChordStartsAtTheFretItsDiagramShows() {
		assertEquals(""
			// an open chord starts at the first fret
			+ "first=1 [0, 1, 0, 2, 3, -1]\n"
			// so does one reaching the 5th fret
			+ "first=1 [0, 5, -1, -1, -1, -1]\n"
			// one reaching the 6th starts at its lowest fret
			+ "first=6 [0, 6, -1, -1, -1, -1]\n"
			// a fretted chord starts at its lowest fret
			+ "first=5 [5, 5, 6, 7, 7, 5]\n"
			// even when it would fit from the first
			+ "first=3 [3, 5, -1, -1, -1, -1]\n"
			// notes 5+ frets above the start are dropped
			+ "first=7 [0, 7, 11, -1, -1, -1]\n"
			// on any string
			+ "first=7 [-1, 7, -1, -1, -1, -1]\n"
			// from the first fret too
			+ "first=1 [1, 5, -1, -1, -1, -1]\n"
			// a chord without notes starts at the first fret
			+ "first=1 [-1, -1, -1, -1, -1, -1]\n"
			, firstFret(0, 1, 0, 2, 3, -1) + "\n" +
			firstFret(0, 5, -1, -1, -1, -1) + "\n" +
			firstFret(0, 6, -1, -1, -1, -1) + "\n" +
			firstFret(5, 5, 6, 7, 7, 5) + "\n" +
			firstFret(3, 5, -1, -1, -1, -1) + "\n" +
			firstFret(0, 7, 11, 12, -1, -1) + "\n" +
			firstFret(12, 7, -1, -1, -1, -1) + "\n" +
			firstFret(1, 5, 6, -1, -1, -1) + "\n" +
			firstFret(-1, -1, -1, -1, -1, -1) + "\n");
	}
}
