package app.tuxguitar.graphics.control;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.graphics.control.painters.TGTempoPainter;
import app.tuxguitar.song.models.TGDuration;

/**
 * Pins the note a tempo mark shows its beat with (TGTempoPainter.paintTempo): an open head for a whole or half
 * note, else a filled one; a stem except for a whole note; one flag for an eighth, two for a sixteenth; a dot when
 * dotted.
 *
 * The note is painted at (10, 20), scale 5; each path is listed as its path style, its first call, its number of
 * calls and the sum of their numbers.
 */
public class TestTempoPainting {

	private static String tempo(int base, boolean dotted) {
		RecordingPainter painter = new RecordingPainter();
		TGTempoPainter.paintTempo(painter, 10, 20, 5, base, dotted);
		StringBuilder paths = new StringBuilder();
		int start = 0;
		for (int i = 0; i < painter.calls().size(); i++) {
			if (painter.calls().get(i).equals("closePath")) {
				paths.append(paths.length() == 0 ? "" : " | ").append(path(painter, start, i));
				start = i + 1;
			}
		}
		return paths.toString();
	}

	// the path between start and end: its style, its first call after the style, how many calls it took and the sum
	// of all the numbers they were given (any moved point changes it)
	private static String path(RecordingPainter painter, int start, int end) {
		double sum = 0;
		for (String call : painter.calls().subList(start + 1, end)) {
			for (String word : call.split(" ")) {
				sum += (word.matches("-?[0-9.]+") ? Double.parseDouble(word) : 0);
			}
		}
		return painter.calls().get(start) + ": " + painter.calls().get(start + 1) + " x" + (end - start + 1) + " sum " + Math.round(sum * 100) / 100.0;
	}

	@Test
	public void eachBeatValueHasItsHeadStemFlagsAndDot() {
		assertEquals(""
			// whole: an open head
			+ "initPath 1: moveTo 10.35 36.3 x7 sum 636.61\n"
			// half: an open head and a stem
			+ "initPath 1: moveTo 10.35 36.3 x7 sum 636.61 | initPath: setAntialias false x5 sum 88.7\n"
			// quarter: a filled head and a stem
			+ "initPath 2: moveTo 10.35 36.3 x7 sum 636.61 | initPath: setAntialias false x5 sum 88.7\n"
			// eighth: a flag
			+ "initPath 2: moveTo 10.35 36.3 x7 sum 636.61 | initPath: setAntialias false x5 sum 88.7 | initPath 2: moveTo 20.21875 31.46875 x24 sum 2210.25\n"
			// sixteenth: two flags
			+ "initPath 2: moveTo 10.35 36.3 x7 sum 636.61 | initPath: setAntialias false x5 sum 88.7 | initPath 2: moveTo 20.21875 31.46875 x24 sum 2210.25 | initPath 2: moveTo 20.21875 34.96875 x24 sum 2378.25\n"
			// dotted quarter: a dot
			+ "initPath 2: moveTo 10.35 36.3 x7 sum 636.61 | initPath: setAntialias false x5 sum 88.7 | initPath 2: moveTo 20.0 34.7 x4 sum 111.4\n"
			, tempo(TGDuration.WHOLE, false) + "\n" +
			tempo(TGDuration.HALF, false) + "\n" +
			tempo(TGDuration.QUARTER, false) + "\n" +
			tempo(TGDuration.EIGHTH, false) + "\n" +
			tempo(TGDuration.SIXTEENTH, false) + "\n" +
			tempo(TGDuration.QUARTER, true) + "\n");
	}
}
