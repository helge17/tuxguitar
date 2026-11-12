package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.QUARTER;
import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.resource.UIImage;

/**
 * Pins what a measure paints around its beats (TGMeasureImpl.paintMeasure): its bar lines (with the measure number
 * on the top staff), thick at the start of the song and of a repeat, thick at the end of the song and of a repeat
 * (with the repeat count), thin at the end of a line; its repeat ending, labelled with its alternatives; its loop
 * start and end markers. With buffering on, the measure is drawn into an image that is kept until it is
 * disposed or the measure starts or stops playing or being the last one.
 *
 * The song has three 4/4 measures of quarter notes. Once printed, a measure is painted alone. The foreground is
 * rgb(1,2,3), measure numbers rgb(4,5,6), the loop start rgb(7,7,7) and the loop end rgb(8,8,8).
 */
public class TestMeasurePainting {

	private static final String NEW_LINE = "\n";

	private final LayoutHarness harness = new LayoutHarness();

	public TestMeasurePainting() {
		harness.songManager.addNewMeasureBeforeEnd(harness.song);
		harness.songManager.addNewMeasureBeforeEnd(harness.song);
		for (int measure = 0; measure < 3; measure++) {
			for (int quarter = 0; quarter < 4; quarter++) {
				harness.note(measure, quarter * QUARTER, TGDuration.QUARTER, 0, 3, 5);
			}
		}
		harness.styles.setForegroundColor(new UIColorModel(1, 2, 3));
		harness.styles.setMeasureNumberColor(new UIColorModel(4, 5, 6));
		harness.styles.setLoopSMarkerColor(new UIColorModel(7, 7, 7));
		harness.styles.setLoopEMarkerColor(new UIColorModel(8, 8, 8));
	}

	// ---- bar lines ----

	@Test
	public void aMeasureInsideALineHasAThinBarLineAtItsStart() {
		assertEquals(""
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setForeground rgb(4,5,6)\n"
			+ "drawString '2' 176.0 89.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(1,2,3)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "moveTo 175.0 85.0\n"
			+ "lineTo 175.0 125.0\n"
			+ "closePath\n"
			+ "setLineWidth 1.0"
			, divisions(TAB, 1));
	}

	@Test
	public void onTheScoreAloneTheBarLinesSpanTheStaff() {
		assertEquals(""
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setForeground rgb(4,5,6)\n"
			+ "drawString '2' 212.0 69.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(1,2,3)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "moveTo 211.0 65.0\n"
			+ "lineTo 211.0 93.0\n"
			+ "closePath\n"
			+ "setLineWidth 1.0\n"
			+ "--\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setForeground rgb(4,5,6)\n"
			+ "drawString '3' 332.0 69.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(1,2,3)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "moveTo 331.0 65.0\n"
			+ "lineTo 331.0 93.0\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 445.0 65.0 1.0 28.0\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 448.0 65.0 3.0 28.0\n"
			+ "closePath\n"
			+ "setLineWidth 1.0"
			, divisions(SCORE, 1) + "\n--\n" + divisions(SCORE, 2));
	}

	@Test
	public void insideALineTheScoreAndTablatureHaveSeparateBarLines() {
		assertEquals(""
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setForeground rgb(4,5,6)\n"
			+ "drawString '2' 212.0 89.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(1,2,3)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "moveTo 211.0 85.0\n"
			+ "lineTo 211.0 113.0\n"
			+ "closePath\n"
			+ "setLineWidth 1.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(1,2,3)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "moveTo 211.0 146.625\n"
			+ "lineTo 211.0 186.625\n"
			+ "closePath\n"
			+ "setLineWidth 1.0"
			, divisions(SCORE | TAB, 1));
	}

	@Test
	public void atTheStartOfALineTheBarLineRunsFromTheScoreDownToTheTablature() {
		harness.song.getMeasureHeader(1).setLineBreak(true);

		assertEquals(""
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setForeground rgb(4,5,6)\n"
			+ "drawString '2' 21.0 235.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(1,2,3)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "moveTo 20.0 231.0\n"
			+ "lineTo 20.0 292.625\n"
			+ "closePath\n"
			+ "setLineWidth 1.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(1,2,3)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "moveTo 20.0 292.625\n"
			+ "lineTo 20.0 332.625\n"
			+ "closePath\n"
			+ "setLineWidth 1.0"
			, divisions(SCORE | TAB, 1));
	}

	@Test
	public void barLinesAndRepeatDotsScaleAndSitAtTheEndOfAWiderMeasure() {
		harness.scale = 2f;
		harness.song.getMeasureHeader(1).setRepeatOpen(true);
		harness.song.getMeasureHeader(1).setRepeatClose(3);
		harness.song.getMeasureHeader(2).setLineBreak(true);

		assertEquals(""
			+ "lineTo 340.0 332.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setForeground rgb(4,5,6)\n"
			+ "drawString '2' 22.0 334.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(1,2,3)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 20.0 332.0 6.0 80.0\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 30.0 332.0 2.0 80.0\n"
			+ "closePath\n"
			+ "setLineWidth 2.0\n"
			+ "initPath 2\n"
			+ "moveTo 42.0 364.0\n"
			+ "addCircle 42.0 364.0 8.0\n"
			+ "moveTo 42.0 380.0\n"
			+ "addCircle 42.0 380.0 8.0\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 328.0 332.0 2.0 80.0\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 334.0 332.0 6.0 80.0\n"
			+ "closePath\n"
			+ "setLineWidth 2.0\n"
			+ "initPath 2\n"
			+ "moveTo 318.0 364.0\n"
			+ "addCircle 318.0 364.0 8.0\n"
			+ "moveTo 318.0 380.0\n"
			+ "addCircle 318.0 380.0 8.0\n"
			+ "closePath\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "drawString 'x3' 324.0 334.0\n"
			+ "setLineWidth 2.0\n"
			+ "--\n"
			+ "lineTo 370.0 150.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setForeground rgb(4,5,6)\n"
			+ "drawString '1' 22.0 152.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(1,2,3)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 20.0 150.0 6.0 80.0\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 30.0 150.0 2.0 80.0\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "moveTo 369.0 150.0\n"
			+ "lineTo 369.0 230.0\n"
			+ "closePath\n"
			+ "setLineWidth 2.0"
			, widened(1) + "\n--\n" + widened(0));
	}

	@Test
	public void theFirstMeasureStartsWithAThickBarLineDownToTheTablature() {
		assertEquals(""
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setForeground rgb(4,5,6)\n"
			+ "drawString '1' 21.0 89.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(1,2,3)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 20.0 85.0 3.0 61.625\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 25.0 85.0 1.0 61.625\n"
			+ "closePath\n"
			+ "setLineWidth 1.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(1,2,3)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 20.0 146.625 3.0 40.0\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 25.0 146.625 1.0 40.0\n"
			+ "closePath\n"
			+ "setLineWidth 1.0"
			, divisions(SCORE | TAB, 0));
	}

	@Test
	public void onTheScoreAloneTheFirstMeasureStartsWithAThickBarLineOfTheStaffOnly() {
		assertEquals(""
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setForeground rgb(4,5,6)\n"
			+ "drawString '1' 21.0 69.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(1,2,3)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 20.0 65.0 3.0 28.0\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 25.0 65.0 1.0 28.0\n"
			+ "closePath\n"
			+ "setLineWidth 1.0"
			, divisions(SCORE, 0));
	}

	@Test
	public void aRepeatOpensWithAThickBarLineAndDots() {
		harness.song.getMeasureHeader(1).setRepeatOpen(true);

		assertEquals(""
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setForeground rgb(4,5,6)\n"
			+ "drawString '2' 176.0 89.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(1,2,3)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 175.0 85.0 3.0 40.0\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 180.0 85.0 1.0 40.0\n"
			+ "closePath\n"
			+ "setLineWidth 1.0\n"
			+ "initPath 2\n"
			+ "moveTo 186.0 101.0\n"
			+ "addCircle 186.0 101.0 4.0\n"
			+ "moveTo 186.0 109.0\n"
			+ "addCircle 186.0 109.0 4.0\n"
			+ "closePath\n"
			+ "setLineWidth 1.0"
			, divisions(TAB, 1));
	}

	@Test
	public void aRepeatClosesWithAThickBarLineDotsAndItsCount() {
		harness.song.getMeasureHeader(1).setRepeatClose(2);

		assertEquals(""
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setForeground rgb(4,5,6)\n"
			+ "drawString '2' 176.0 89.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(1,2,3)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "moveTo 175.0 85.0\n"
			+ "lineTo 175.0 125.0\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 309.0 85.0 1.0 40.0\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 312.0 85.0 3.0 40.0\n"
			+ "closePath\n"
			+ "setLineWidth 1.0\n"
			+ "initPath 2\n"
			+ "moveTo 304.0 101.0\n"
			+ "addCircle 304.0 101.0 4.0\n"
			+ "moveTo 304.0 109.0\n"
			+ "addCircle 304.0 109.0 4.0\n"
			+ "closePath\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "drawString 'x2' 303.0 89.0\n"
			+ "setLineWidth 1.0"
			, divisions(TAB, 1));
	}

	@Test
	public void theLastMeasureEndsWithAThickBarLine() {
		assertEquals(""
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setForeground rgb(4,5,6)\n"
			+ "drawString '3' 296.0 89.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(1,2,3)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "moveTo 295.0 85.0\n"
			+ "lineTo 295.0 125.0\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 409.0 85.0 1.0 40.0\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 412.0 85.0 3.0 40.0\n"
			+ "closePath\n"
			+ "setLineWidth 1.0"
			, divisions(TAB, 2));
	}

	@Test
	public void theLastMeasureOfALineEndsWithAThinBarLine() {
		harness.song.getMeasureHeader(1).setLineBreak(true);

		assertEquals(""
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setForeground rgb(4,5,6)\n"
			+ "drawString '1' 21.0 89.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(1,2,3)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 20.0 85.0 3.0 40.0\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "addRectangle 25.0 85.0 1.0 40.0\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "moveTo 174.0 85.0\n"
			+ "lineTo 174.0 125.0\n"
			+ "closePath\n"
			+ "setLineWidth 1.0"
			, divisions(TAB, 0));
	}

	@Test
	public void aOneStringTablatureHasBarLinesAStringAboveAndBelow() {
		harness.song.getTrack(0).setStrings(harness.songManager.createDefaultInstrumentStrings(1));
		for (int measure = 0; measure < 3; measure++) {
			for (int quarter = 0; quarter < 4; quarter++) {
				harness.beatAt(measure, quarter * QUARTER).getVoice(0).getNote(0).setString(1);
			}
		}

		assertEquals(""
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setForeground rgb(4,5,6)\n"
			+ "drawString '2' 176.0 61.0\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(1,2,3)\n"
			+ "setForeground rgb(1,2,3)\n"
			+ "setLineWidth 0.0\n"
			+ "initPath 3\n"
			+ "setAntialias false\n"
			+ "moveTo 175.0 57.0\n"
			+ "lineTo 175.0 73.0\n"
			+ "closePath\n"
			+ "setLineWidth 1.0"
			, divisions(TAB, 1));
	}

	// ---- repeat endings and loop markers ----

	@Test
	public void aRepeatEndingIsLabelledWithItsAlternatives() {
		harness.song.getMeasureHeader(1).setRepeatAlternative((1 << 0) | (1 << 2));

		String alternatives = around(TAB, 1, "drawString '1, 3'", 10, 0);
		harness.song.getMeasureHeader(1).setRepeatAlternative(1 << 0);

		// a single first ending is labelled, and raised the same
		assertEquals("drawString '1' 205.0 71.0", around(TAB, 1, "drawString '1' 205.0", 0, 0));
		assertEquals(""
			+ "setForeground rgb(1,2,3)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "initPath\n"
			+ "setAntialias false\n"
			+ "moveTo 200.0 80.0\n"
			+ "lineTo 200.0 65.0\n"
			+ "moveTo 200.0 65.0\n"
			+ "lineTo 295.0 65.0\n"
			+ "closePath\n"
			+ "drawString '1, 3' 205.0 71.0"
			, alternatives);
	}

	@Test
	public void loopMarkersAreSquaresAtTheStartAndEnd() {
		harness.loopStart = 2;
		harness.loopEnd = 2;
		harness.styles.setLoopMarkerSpacing(6);

		assertEquals(""
			+ "setBackground rgb(7,7,7)\n"
			+ "initPath 2\n"
			+ "addRectangle 178.0 65.0 5.0 5.0\n"
			+ "closePath\n"
			+ "--\n"
			+ "setBackground rgb(8,8,8)\n"
			+ "initPath 2\n"
			+ "addRectangle 290.0 65.0 5.0 5.0\n"
			+ "closePath"
			, around(TAB, 1, "setBackground rgb(7,7,7)", 0, 3) + "\n--\n" + around(TAB, 1, "setBackground rgb(8,8,8)", 0, 3));
	}

	// ---- buffered painting ----

	@Test
	public void aBufferedMeasureIsRepaintedOnlyWhenItsBufferIsGoneOrItStartsOrStopsPlayingOrBeingLast() {
		harness.styles.setBufferEnabled(true);
		TGLayout layout = harness.screen(TAB);
		TGMeasureImpl measure = harness.measure(2);
		List<String> repainted = new ArrayList<String>();

		repainted.add(repaints(layout, measure, () -> { }));
		repainted.add(repaints(layout, measure, () -> layout.getResourceBuffer().getResource(measure.getBuffer().getRegistryKey()).dispose()));
		repainted.add(repaints(layout, measure, () -> harness.playingMeasure = 3));
		repainted.add(repaints(layout, measure, () -> { }));
		repainted.add(repaints(layout, measure, () -> harness.playingMeasure = -1));
		repainted.add(repaints(layout, measure, () -> harness.songManager.addNewMeasureBeforeEnd(harness.song)));
		repainted.add(repaints(layout, measure, () -> { }));

		assertEquals("[kept, repainted, repainted, kept, repainted, repainted, kept]", repainted.toString());
		// a kept image is drawn all the same
		assertEquals("[drawImage 290.0 5.0]", harness.painter.callsStartingWith("drawImage").toString());
	}

	@Test
	public void aBufferedMeasureIsDrawnIntoAnImageAsWideAsTheMeasureWithItsSpacing() {
		harness.styles.setBufferEnabled(true);
		TGLayout layout = harness.screen(TAB);
		TGMeasureImpl measure = harness.measure(1);
		measure.setSpacing(40f);
		layout.getResourceBuffer().getResource(measure.getBuffer().getRegistryKey()).dispose();
		measure.paintMeasure(layout, harness.painter);

		UIImage image = layout.getResourceBuffer().getResource(measure.getBuffer().getRegistryKey());
		RecordingPainter contents = (RecordingPainter) image.createPainter();
		assertEquals(""
			+ "image(160.0x91.0) addRectangle 0.0 0.0 160.0 91.0 / moveTo 0.0 20.0 lineTo 160.0 20.0"
			, image + " " + contents.callsStartingWith("addRectangle").get(0) + " / " + contents.callsStartingWith("moveTo").get(0)
			+ " " + contents.callsStartingWith("lineTo").get(0));
	}

	// paints the measure again after the change: whether it made a new buffer image
	private String repaints(TGLayout layout, TGMeasureImpl measure, Runnable change) {
		change.run();
		Object before = layout.getResourceBuffer().getResource(measure.getBuffer().getRegistryKey());
		harness.painter.clear();
		measure.paintMeasure(layout, harness.painter);
		return (layout.getResourceBuffer().getResource(measure.getBuffer().getRegistryKey()) == before ? "kept" : "repainted");
	}

	// ---- painting ----

	private List<String> paint(int style, int measure) {
		harness.print(style);
		harness.painter.clear();
		harness.measure(measure).paintMeasure(harness.layout(), harness.painter);
		return harness.painter.calls();
	}

	// the calls from before to after the first one starting with the given call
	private String around(int style, int measure, String call, int before, int after) {
		List<String> calls = paint(style, measure);
		int index = 0;
		while (!calls.get(index).startsWith(call)) {
			index++;
		}
		return String.join("\n", calls.subList(index - before, index + after + 1));
	}

	// the measure printed at scale 2 on the tablature, then widened by 40: its first staff line, then its bar lines
	private String widened(int measure) {
		harness.print(TAB);
		harness.measure(measure).setSpacing(40f);
		harness.painter.clear();
		harness.measure(measure).paintMeasure(harness.layout(), harness.painter);
		List<String> calls = harness.painter.calls();
		return calls.get(calls.indexOf("initPath") + 3) + NEW_LINE + barLines(calls);
	}

	private String divisions(int style, int measure) {
		return barLines(paint(style, measure));
	}

	// the bar lines: from the measure number (or the divisions style) up to the tempo, if any, else to the end
	private static String barLines(List<String> calls) {
		int start = calls.indexOf("setForeground rgb(4,5,6)") - 2;
		int end = start;
		while (end < calls.size() && !(calls.get(end).startsWith("setFont") && calls.get(end + 1).startsWith("setForeground"))) {
			end++;
		}
		return String.join(NEW_LINE, calls.subList(start, end));
	}
}
