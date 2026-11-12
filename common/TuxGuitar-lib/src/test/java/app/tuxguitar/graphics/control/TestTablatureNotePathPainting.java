package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.graphics.control.painters.TGNumberPainter;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.resource.UIInset;

/**
 * Pins how a tablature note value is drawn with the path renderer (TGNoteImpl.paintTablatureNoteValue when
 * TGLayoutStyles.isTabNotePathRendererEnabled): centered on its point, as tall as the string spacing less 2, as wide
 * as its digits (a dead note: 6 units, scaled), over a background box of that size; the fret as digit paths, a dead
 * note as a cross; a ghost note within parentheses 3 units (scaled) outside it on each side. The value reports that
 * box as its margin, and is colored as playing while running.
 *
 * Fret 5 on string 3 is painted alone at (40, 20); at scale 1 the note is 6 tall and the digit 5 is 4.1673155 wide.
 * Each result starts with the margin (top bottom left right), then the calls. The play note color is red.
 */
public class TestTablatureNotePathPainting {

	private static final String BLACK = "rgb(0,0,0)";
	private static final String RED = "rgb(255,0,0)";

	private final LayoutHarness harness = new LayoutHarness();
	private final TGNoteImpl note = harness.note(0, TGDuration.QUARTER, 0, 3, 5);

	public TestTablatureNotePathPainting() {
		harness.styles.setPlayNoteColor(new UIColorModel(255, 0, 0));
		harness.styles.setTabNotePathRendererEnabled(true);
	}

	@Test
	public void aFretIsDrawnAsItsDigitPaths() {
		assertEquals(""
			+ "margin 3.0 3.0 2.0836577 2.0836577\n"
			+ background("37.916344 17.0 4.1673155 6.0") + "\n"
			+ style(BLACK) + "\n"
			+ digits(37.916344f, 17f, 6f)
			, paint(false));
	}

	@Test
	public void aDeadNoteIsDrawnAsACross() {
		note.getEffect().setDeadNote(true);

		assertEquals(""
			+ "margin 3.0 3.0 3.0 3.0\n"
			+ background("37.0 17.0 6.0 6.0") + "\n"
			+ style(BLACK) + "\n"
			+ "initPath 1\n"
			+ "moveTo 37.0 17.0\n"
			+ "lineTo 43.0 23.0\n"
			+ "moveTo 43.0 17.0\n"
			+ "lineTo 37.0 23.0\n"
			+ "closePath"
			, paint(false));
	}

	@Test
	public void aGhostNoteIsWithinParenthesesAndColoredWhileRunning() {
		note.getEffect().setGhostNote(true);

		assertEquals(""
			+ "margin 3.0 3.0 5.0836577 5.0836577\n"
			+ background("34.916344 17.0 10.1673155 6.0") + "\n"
			+ style(RED) + "\n"
			+ digits(37.916344f, 17f, 6f) + "\n"
			+ "initPath 2\n"
			+ "moveTo 37.916344 17.0\n"
			+ "cubicTo 32.916344 17.0 32.916344 23.0 37.916344 23.0\n"
			+ "cubicTo 34.916344 23.0 34.916344 17.0 37.916344 17.0\n"
			+ "closePath\n"
			+ "initPath 2\n"
			+ "moveTo 42.083656 17.0\n"
			+ "cubicTo 47.083656 17.0 47.083656 23.0 42.083656 23.0\n"
			+ "cubicTo 45.083656 23.0 45.083656 17.0 42.083656 17.0\n"
			+ "closePath"
			, paint(true));
	}

	@Test
	public void atScaleTwoAGhostDeadNoteScalesItsCrossAndParentheses() {
		note.getEffect().setDeadNote(true);
		note.getEffect().setGhostNote(true);
		harness.scale = 2f;

		assertEquals(""
			+ "margin 7.0 7.0 12.0 12.0\n"
			+ background("28.0 13.0 24.0 14.0") + "\n"
			+ style(BLACK) + "\n"
			+ "initPath 1\n"
			+ "moveTo 34.0 13.0\n"
			+ "lineTo 46.0 27.0\n"
			+ "moveTo 46.0 13.0\n"
			+ "lineTo 34.0 27.0\n"
			+ "closePath\n"
			+ "initPath 2\n"
			+ "moveTo 34.0 13.0\n"
			+ "cubicTo 24.0 13.0 24.0 27.0 34.0 27.0\n"
			+ "cubicTo 28.0 27.0 28.0 13.0 34.0 13.0\n"
			+ "closePath\n"
			+ "initPath 2\n"
			+ "moveTo 46.0 13.0\n"
			+ "cubicTo 56.0 13.0 56.0 27.0 46.0 27.0\n"
			+ "cubicTo 52.0 27.0 52.0 13.0 46.0 13.0\n"
			+ "closePath"
			, paint(false));
	}

	// ---- helpers ----

	private static String background(String rectangle) {
		return "setBackground rgb(0,0,0)\ninitPath 2\naddRectangle " + rectangle + "\nclosePath";
	}

	private static String style(String color) {
		return "setForeground " + color + "\nsetBackground " + color;
	}

	// the digit paths of fret 5 with their top left corner at (x, y), the given size tall
	private static String digits(float x, float y, float size) {
		RecordingPainter painter = new RecordingPainter();
		TGNumberPainter.paintDigits(5, painter, x, y, size);
		return String.join("\n", painter.calls());
	}

	// lays the song out, then paints the note value alone at (40, 20)
	private String paint(boolean running) {
		TGLayout layout = harness.update(TAB);
		UIInset margin = new UIInset();
		harness.painter.clear();
		note.paintTablatureNoteValue(layout, harness.painter, margin, 40, 20, running);
		return "margin " + margin.getTop() + " " + margin.getBottom() + " " + margin.getLeft() + " " + margin.getRight() + "\n"
			+ String.join("\n", harness.painter.calls());
	}
}
