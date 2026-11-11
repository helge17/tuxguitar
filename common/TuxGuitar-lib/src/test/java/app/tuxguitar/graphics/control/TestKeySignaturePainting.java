package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.graphics.control.painters.TGKeySignaturePainter;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.ui.resource.UIPainter;

/**
 * Pins how a measure paints its key signature on the score (TGMeasureImpl.paintKeySignature): key 1-7 is that many
 * sharps, key 8-14 that many flats (minus 7), each at its clef's line, left to right. A key change first cancels,
 * with naturals, the previous key's accidentals the new key does not keep.
 *
 * The song is two measures; once printed, the painted key signature symbols are read back in order as
 * "kind x,y" (the symbol's first point), a line per measure.
 */
public class TestKeySignaturePainting {

	private final LayoutHarness harness = new LayoutHarness();

	private static final String SHARP = "sharp";
	private static final String FLAT = "flat";
	private static final String NATURAL = "natural";

	private String paint(int clef, int key1, int key2) {
		return paint(SCORE, clef, key1, key2);
	}

	private String paint(int style, int clef, int key1, int key2) {
		harness.songManager.addNewMeasureBeforeEnd(harness.song);
		harness.measure(0).setClef(clef);
		harness.measure(1).setClef(clef);
		harness.measure(0).setKeySignature(key1);
		harness.measure(1).setKeySignature(key2);
		List<String> calls = harness.print(style).calls();
		float x2 = harness.measure(1).getPosX();

		StringBuilder first = new StringBuilder();
		StringBuilder second = new StringBuilder();
		for (String symbol : symbols(calls)) {
			float x = Float.parseFloat(symbol.split(" ")[1].split(",")[0]);
			(x < x2 ? first : second).append(symbol).append("; ");
		}
		return first + "|\n" + second + "|";
	}

	// every fill path drawn with the same commands as a sharp, flat or natural
	private static List<String> symbols(List<String> calls) {
		List<String> symbols = new ArrayList<String>();
		String fill = "initPath " + UIPainter.PATH_FILL;
		for (int i = 0; i < calls.size(); i++) {
			if (calls.get(i).equals(fill)) {
				int end = i + 1 + calls.subList(i + 1, calls.size()).indexOf("closePath");
				String kind = kind(commands(calls.subList(i + 1, end)));
				if (kind != null) {
					String[] point = calls.get(i + 1).split(" ");
					symbols.add(kind + " " + point[1] + "," + point[2]);
				}
				i = end;
			}
		}
		return symbols;
	}

	private static String kind(List<String> commands) {
		if (commands.equals(reference(SHARP))) {
			return SHARP;
		}
		if (commands.equals(reference(FLAT))) {
			return FLAT;
		}
		return (commands.equals(reference(NATURAL)) ? NATURAL : null);
	}

	private static List<String> reference(String kind) {
		RecordingPainter painter = new RecordingPainter();
		if (kind.equals(SHARP)) {
			TGKeySignaturePainter.paintSharp(painter, 0, 0, 1);
		} else if (kind.equals(FLAT)) {
			TGKeySignaturePainter.paintFlat(painter, 0, 0, 1);
		} else {
			TGKeySignaturePainter.paintNatural(painter, 0, 0, 1);
		}
		return commands(painter.calls());
	}

	private static List<String> commands(List<String> calls) {
		List<String> commands = new ArrayList<String>();
		for (String call : calls) {
			commands.add(call.split(" ")[0]);
		}
		return commands;
	}

	@Test
	public void cMajorPaintsNothing() {
		assertEquals("|\n|", paint(TGMeasure.CLEF_TREBLE, 0, 0));
	}

	@Test
	public void sharpsThenFewerSharps() {
		assertEquals(""
			+ "sharp 84.03134,59.42729; sharp 89.28134,69.92729; sharp 94.53134,55.92729; |\n"
			+ "natural 204.27216,68.515076; natural 209.52216,54.51508; sharp 217.53134,59.42729; |"
			, paint(TGMeasure.CLEF_TREBLE, 3, 1));
	}

	@Test
	public void flatsThenFewerFlats() {
		assertEquals(""
			+ "flat 81.25257,70.40469; flat 86.50257,59.904694; flat 91.75257,73.90469; |\n"
			+ "natural 204.27216,61.51508; natural 209.52216,75.515076; flat 214.75256,70.40469; |"
			, paint(TGMeasure.CLEF_TREBLE, 10, 8));
	}

	@Test
	public void sharpsThenFlats() {
		assertEquals(""
			+ "sharp 84.03134,59.42729; sharp 89.28134,69.92729; |\n"
			+ "natural 198.27216,58.01508; natural 203.52216,68.515076; flat 208.75256,70.40469; flat 214.00256,59.904694; |"
			, paint(TGMeasure.CLEF_TREBLE, 2, 9));
	}

	@Test
	public void flatsThenSharps() {
		assertEquals(""
			+ "flat 81.25257,70.40469; flat 86.50257,59.904694; |\n"
			+ "natural 198.27216,72.015076; natural 203.52216,61.51508; sharp 211.53134,59.42729; sharp 216.78134,69.92729; |"
			, paint(TGMeasure.CLEF_TREBLE, 9, 2));
	}

	@Test
	public void allSharpsThenC() {
		assertEquals(""
			+ "sharp 84.03134,66.42729; sharp 89.28134,76.92729; sharp 94.53134,62.92729; sharp 99.78134,73.42729; "
			+ "sharp 105.03134,83.92729; sharp 110.28134,69.92729; sharp 115.53134,80.42729; |\n"
			+ "natural 228.27216,65.015076; natural 233.52216,75.515076; natural 238.77216,61.51508; natural 244.02216,72.015076; "
			+ "natural 249.27216,82.515076; natural 254.52216,68.515076; natural 259.77216,79.015076; |"
			, paint(TGMeasure.CLEF_BASS, 7, 0));
	}

	@Test
	public void allFlatsThenC() {
		assertEquals(""
			+ "flat 81.25257,73.90469; flat 86.50257,63.404694; flat 91.75257,77.40469; flat 97.00257,66.90469; "
			+ "flat 102.25257,80.90469; flat 107.50257,70.40469; flat 112.75257,84.40469; |\n"
			+ "natural 228.27216,75.515076; natural 233.52216,65.015076; natural 238.77216,79.015076; natural 244.02216,68.515076; "
			+ "natural 249.27216,82.515076; natural 254.52216,72.015076; natural 259.77216,86.015076; |"
			, paint(TGMeasure.CLEF_ALTO, 14, 0));
	}

	@Test
	public void moreSharpsAddOnlyTheNewOnes() {
		assertEquals(""
			+ "sharp 84.03134,80.42729; |\n"
			+ "sharp 195.03134,80.42729; sharp 200.28134,66.42729; sharp 205.53134,76.92729; |"
			, paint(TGMeasure.CLEF_TENOR, 1, 3));
	}

	@Test
	public void theSameKeyIsNotRepeated() {
		assertEquals(""
			+ "sharp 84.03134,59.42729; sharp 89.28134,69.92729; sharp 94.53134,55.92729; sharp 99.78134,66.42729; |\n"
			+ "|"
			, paint(TGMeasure.CLEF_TREBLE, 4, 4));
	}

	@Test
	public void theTablatureHasNoKeySignature() {
		assertEquals("|\n|", paint(TAB, TGMeasure.CLEF_TREBLE, 4, 11));
	}
}
