package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.QUARTER;
import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.ui.resource.UIColorModel;

/**
 * Pins how a track paints a selection of beats (TGTrackImpl.paintBeatSelection): in one measure, a translucent box
 * then its outline from the left of the first beat to the right of the last one; across measures, the first measure
 * from its beat to its end, the measures between in full, the last up to its beat, each outlined on its closed
 * sides only. Vertically, the selection spans from just above the top staff to just below the bottom one: the
 * score, the tablature, or the score down to the tablature.
 *
 * The song has three 4/4 measures of quarter notes, printed on one line. The selection color is blue.
 */
public class TestSelectionPainting {

	private static final String SEPARATOR = "\n--\n";

	private final LayoutHarness harness = new LayoutHarness();

	public TestSelectionPainting() {
		harness.songManager.addNewMeasureBeforeEnd(harness.song);
		harness.songManager.addNewMeasureBeforeEnd(harness.song);
		for (int measure = 0; measure < 3; measure++) {
			for (int quarter = 0; quarter < 4; quarter++) {
				harness.note(measure, quarter * QUARTER, TGDuration.QUARTER, 0, 3, 5);
			}
		}
		harness.styles.setSelectionColor(new UIColorModel(0, 0, 255));
	}

	@Test
	public void inOneMeasureTheSelectionIsABoxOverTheStavesShown() {
		String tablature = selection(TAB, beat(0, 1), beat(0, 2));
		String score = selection(SCORE, beat(0, 1), beat(0, 2));
		String both = selection(SCORE | TAB, beat(0, 1), beat(0, 2));

		assertEquals(box(92.5f, 77, 40, 56) + SEPARATOR + box(128.5f, 58, 40, 42) + SEPARATOR + box(128.5f, 78, 40, 116.625f),
			tablature + SEPARATOR + score + SEPARATOR + both);
	}

	@Test
	public void aSelectionAcrossMeasuresIsOpenWhereItContinues() {
		assertEquals(""
			+ fill(112.5f, 77, 62.5f, 56)
			+ outline()
			+ "moveTo 175.0 133.0\n"
			+ "lineTo 112.5 133.0\n"
			+ "lineTo 112.5 77.0\n"
			+ "lineTo 175.0 77.0\n"
			+ "closePath\n"
			+ fill(175, 77, 120, 56)
			+ outline()
			+ "moveTo 175.0 77.0\n"
			+ "lineTo 295.0 77.0\n"
			+ "moveTo 175.0 133.0\n"
			+ "lineTo 295.0 133.0\n"
			+ "closePath\n"
			+ fill(295, 77, 57.5f, 56)
			+ outline()
			+ "moveTo 295.0 133.0\n"
			+ "lineTo 352.5 133.0\n"
			+ "lineTo 352.5 77.0\n"
			+ "lineTo 295.0 77.0\n"
			+ "closePath"
			, selection(TAB, beat(0, 2), beat(2, 1)));
	}

	@Test
	public void theSelectionScalesAndFollowsTheBeatsOfAWiderMeasure() {
		harness.scale = 2f;
		harness.print(TAB);
		TGLayout layout = harness.layout();
		harness.measure(0).setSpacing(40f);
		harness.measure(1).setSpacing(40f);
		harness.painter.clear();
		((TGTrackImpl) harness.song.getTrack(0)).paintBeatSelection(layout, harness.painter, beat(0, 1), beat(1, 2));

		assertEquals(""
			+ "addRectangle 175.0 134.0 195.0 112.0\n"
			+ "--\n"
			+ "addRectangle 20.0 316.0 175.0 112.0\n"
			+ "--\n"
			+ "setLineWidth 2.0\n"
			+ "--\n"
			+ "setLineWidth 2.0\n"
			+ "--\n"
			+ "setLineWidth 2.0\n"
			+ "--\n"
			+ "setLineWidth 2.0"
			, String.join(SEPARATOR, harness.painter.callsStartingWith("addRectangle")) + SEPARATOR
			+ String.join(SEPARATOR, harness.painter.callsStartingWith("setLineWidth")));
	}

	// ---- expected drawings ----

	// a translucent box, then its outline
	private static String box(float x, float y, float width, float height) {
		return fill(x, y, width, height) + outline() + "addRectangle " + x + " " + y + " " + width + " " + height + "\nclosePath";
	}

	private static String fill(float x, float y, float width, float height) {
		return path(2) + "setAlpha 18\naddRectangle " + x + " " + y + " " + width + " " + height + "\nclosePath\n";
	}

	private static String outline() {
		return path(1) + "setAlpha 255\n";
	}

	private static String path(int style) {
		return ""
			+ "setLineWidth 1.0\n"
			+ "setForeground rgb(0,0,255)\n"
			+ "setBackground rgb(0,0,255)\n"
			+ "initPath " + style + "\n"
			+ "setAntialias false\n";
	}

	// ---- painting ----

	private TGBeat beat(int measure, int quarter) {
		return harness.beatAt(measure, quarter * QUARTER);
	}

	private String selection(int style, TGBeat from, TGBeat to) {
		harness.print(style);
		harness.painter.clear();
		((TGTrackImpl) harness.song.getTrack(0)).paintBeatSelection(harness.layout(), harness.painter, from, to);
		return String.join("\n", harness.painter.calls());
	}
}
