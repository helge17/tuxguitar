package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.QUARTER;
import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;

/**
 * Pins how a percussion note is drawn on the score (TGNoteImpl.paintPercussionScoreNote), by the drum map's
 * render kind of its value: note head, X, slanted diamond, triangle, effect cymbal X, with an open circle,
 * closed cross or circle around; filled from the quarter on.
 *
 * The drum is the last note of a percussion measure; once the measure is printed, the note is painted alone
 * at (0, 0), and read back as the painter's calls, "; " separated, for a quarter then for a half.
 */
public class TestPercussionNoteHeads {

	private LayoutHarness harness;

	private String drum(int value) {
		return "quarter: " + paint(value, TGDuration.QUARTER, -1) + "\nhalf: " + paint(value, TGDuration.HALF, -1);
	}

	// the drum on the second beat, after a quarter of the given drum (none when -1)
	private String paint(int value, int durationValue, int previousValue) {
		percussion();
		if (previousValue >= 0) {
			harness.note(0, TGDuration.QUARTER, 0, 1, previousValue);
		} else {
			harness.rest(0, TGDuration.QUARTER, 0);
		}
		return paintAlone(harness.note(QUARTER, durationValue, 0, 1, value));
	}

	private void percussion() {
		harness = new LayoutHarness();
		harness.percussion();
	}

	private String paintAlone(TGNoteImpl note) {
		harness.print(SCORE);
		harness.painter.clear();
		note.paint(harness.layout(), harness.painter, 0, 0);
		return String.join("; ", harness.painter.calls());
	}

	// a quarter of the drum opening a second measure, after a first measure ending with a quarter of the other drum
	private String paintAfterBarLine(int value, int previousValue) {
		percussion();
		harness.note(3 * QUARTER, TGDuration.QUARTER, 0, 1, previousValue);
		harness.songManager.addNewMeasureBeforeEnd(harness.song);
		TGMeasure measure = harness.measure(1);
		TGBeat beat = harness.factory.newBeat();
		beat.setPreciseStart(measure.getPreciseStart());
		harness.measureManager.addBeat(measure, beat);
		beat.getVoice(0).setEmpty(false);
		beat.getVoice(0).getDuration().setValue(TGDuration.QUARTER);
		TGNote note = harness.factory.newNote();
		note.setString(1);
		note.setValue(value);
		beat.getVoice(0).addNote(note);
		return paintAlone((TGNoteImpl) note);
	}

	// the cross above a closed hi-hat is two lines more, after the X of its head
	private static boolean hasCross(String calls) {
		return calls.split("moveTo", -1).length - 1 == 4;
	}

	@Test
	public void aClosedHiHatOpeningTheSongHasACross() {
		percussion();
		assertEquals(true, hasCross(paintAlone(harness.note(0, TGDuration.QUARTER, 0, 1, 42))));
	}

	@Test
	public void aClosedHiHatAfterOneAcrossTheBarLineHasNoCross() {
		assertEquals(false, hasCross(paintAfterBarLine(42, 42)));
		assertEquals(true, hasCross(paintAfterBarLine(42, 38)));
	}

	@Test
	public void aDrumIsARoundNoteHead() {
		assertEquals(""
			+ "quarter: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 2; moveTo 100.58 31.578001; cubicTo 100.58 32.649002 101.6258 33.72 102.659004 33.72; cubicTo 105.809006 33.72 108.959 31.578001 108.959 29.499; cubicTo 108.959 28.4658 107.888 27.42 106.880005 27.42; cubicTo 103.73 27.42 100.58 29.499 100.58 31.578001; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)\n"
			+ "half: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 1; moveTo 101.0 31.396; cubicTo 101.0 32.348 101.9296 33.3 102.848 33.3; cubicTo 105.648 33.3 108.448 31.396 108.448 29.548; cubicTo 108.448 28.6296 107.496 27.7 106.6 27.7; cubicTo 103.8 27.7 101.0 29.548 101.0 31.396; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)"
			, drum(38));
	}

	@Test
	public void aCrossStickIsCircled() {
		assertEquals(""
			+ "quarter: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 2; moveTo 100.58 31.578001; cubicTo 100.58 32.649002 101.6258 33.72 102.659004 33.72; cubicTo 105.809006 33.72 108.959 31.578001 108.959 29.499; cubicTo 108.959 28.4658 107.888 27.42 106.880005 27.42; cubicTo 103.73 27.42 100.58 29.499 100.58 31.578001; closePath; setLineWidth 1.0; initPath 1; addCircle 104.5 29.975 11.2; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)\n"
			+ "half: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 1; moveTo 101.0 31.396; cubicTo 101.0 32.348 101.9296 33.3 102.848 33.3; cubicTo 105.648 33.3 108.448 31.396 108.448 29.548; cubicTo 108.448 28.6296 107.496 27.7 106.6 27.7; cubicTo 103.8 27.7 101.0 29.548 101.0 31.396; closePath; setLineWidth 1.0; initPath 1; addCircle 104.5 29.975 11.2; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)"
			, drum(37));
	}

	@Test
	public void aFootHiHatIsAnX() {
		assertEquals(""
			+ "quarter: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 1; moveTo 101.833336 49.0; lineTo 106.833336 54.0; moveTo 101.833336 54.0; lineTo 106.833336 49.0; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)\n"
			+ "half: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 1; moveTo 101.833336 49.0; lineTo 106.833336 54.0; moveTo 101.833336 54.0; lineTo 106.833336 49.0; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)"
			, drum(44));
	}

	@Test
	public void anOpenHiHatHasACircleAbove() {
		assertEquals(""
			+ "quarter: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 1; moveTo 101.833336 16.0; lineTo 106.833336 21.0; moveTo 101.833336 21.0; lineTo 106.833336 16.0; closePath; setLineWidth 1.0; initPath 1; addCircle 105.06 10.379999 3.5; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)\n"
			+ "half: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 1; moveTo 101.833336 16.0; lineTo 106.833336 21.0; moveTo 101.833336 21.0; lineTo 106.833336 16.0; closePath; setLineWidth 1.0; initPath 1; addCircle 105.06 10.379999 3.5; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)"
			, drum(46));
	}

	@Test
	public void aClosedHiHatHasACrossAbove() {
		assertEquals(""
			+ "quarter: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 1; moveTo 101.833336 16.0; lineTo 106.833336 21.0; moveTo 101.833336 21.0; lineTo 106.833336 16.0; closePath; setLineWidth 1.0; initPath 1; moveTo 102.75 10.379999; lineTo 107.37 10.379999; moveTo 105.06 12.69; lineTo 105.06 8.07; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)\n"
			+ "half: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 1; moveTo 101.833336 16.0; lineTo 106.833336 21.0; moveTo 101.833336 21.0; lineTo 106.833336 16.0; closePath; setLineWidth 1.0; initPath 1; moveTo 102.75 10.379999; lineTo 107.37 10.379999; moveTo 105.06 12.69; lineTo 105.06 8.07; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)"
			, drum(42));
	}

	@Test
	public void aClosedHiHatRepeatedHasNoCross() {
		assertEquals(""
			+ "setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 1; moveTo 101.833336 16.0; lineTo 106.833336 21.0; moveTo 101.833336 21.0; lineTo 106.833336 16.0; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)"
			, paint(42, TGDuration.QUARTER, 42));
	}

	@Test
	public void aCrashIsAnEffectCymbalX() {
		assertEquals(""
			+ "quarter: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 1; moveTo 101.833336 16.0; lineTo 106.833336 21.0; moveTo 101.833336 21.0; lineTo 106.833336 16.0; moveTo 101.833336 16.0; addRectangle 101.833336 16.0 1.25 1.25; moveTo 105.583336 19.75; addRectangle 105.583336 19.75 1.25 1.25; moveTo 101.833336 19.75; addRectangle 101.833336 19.75 1.25 1.25; moveTo 105.583336 16.0; addRectangle 105.583336 16.0 1.25 1.25; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)\n"
			+ "half: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 1; moveTo 101.833336 16.0; lineTo 106.833336 21.0; moveTo 101.833336 21.0; lineTo 106.833336 16.0; moveTo 101.833336 16.0; addRectangle 101.833336 16.0 1.25 1.25; moveTo 105.583336 19.75; addRectangle 105.583336 19.75 1.25 1.25; moveTo 101.833336 19.75; addRectangle 101.833336 19.75 1.25 1.25; moveTo 105.583336 16.0; addRectangle 105.583336 16.0 1.25 1.25; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)"
			, drum(49));
	}

	@Test
	public void aChinaIsACircledEffectCymbalX() {
		assertEquals(""
			+ "quarter: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 1; moveTo 101.833336 16.0; lineTo 106.833336 21.0; moveTo 101.833336 21.0; lineTo 106.833336 16.0; moveTo 101.833336 16.0; addRectangle 101.833336 16.0 1.25 1.25; moveTo 105.583336 19.75; addRectangle 105.583336 19.75 1.25 1.25; moveTo 101.833336 19.75; addRectangle 101.833336 19.75 1.25 1.25; moveTo 105.583336 16.0; addRectangle 105.583336 16.0 1.25 1.25; closePath; setLineWidth 1.0; initPath 1; addCircle 104.5 17.975 11.2; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)\n"
			+ "half: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 1; moveTo 101.833336 16.0; lineTo 106.833336 21.0; moveTo 101.833336 21.0; lineTo 106.833336 16.0; moveTo 101.833336 16.0; addRectangle 101.833336 16.0 1.25 1.25; moveTo 105.583336 19.75; addRectangle 105.583336 19.75 1.25 1.25; moveTo 101.833336 19.75; addRectangle 101.833336 19.75 1.25 1.25; moveTo 105.583336 16.0; addRectangle 105.583336 16.0 1.25 1.25; closePath; setLineWidth 1.0; initPath 1; addCircle 104.5 17.975 11.2; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)"
			, drum(52));
	}

	@Test
	public void aRideBellIsASlantedDiamond() {
		assertEquals(""
			+ "quarter: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 3; moveTo 101.56 20.0; lineTo 104.64 22.800001; lineTo 107.72 20.0; lineTo 104.64 17.2; lineTo 101.56 20.0; lineTo 104.64 22.800001; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)\n"
			+ "half: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 1; moveTo 101.56 20.0; lineTo 104.64 22.800001; lineTo 107.72 20.0; lineTo 104.64 17.2; lineTo 101.56 20.0; lineTo 104.64 22.800001; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)"
			, drum(53));
	}

	@Test
	public void aTambourineIsATriangle() {
		assertEquals(""
			+ "quarter: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 3; moveTo 107.53333 35.96; lineTo 101.933334 35.96; lineTo 104.73333 31.110258; lineTo 107.53333 35.96; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)\n"
			+ "half: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 1; moveTo 107.53333 35.96; lineTo 101.933334 35.96; lineTo 104.73333 31.110258; lineTo 107.53333 35.96; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)"
			, drum(54));
	}

	@Test
	public void aMutedTriangleIsATriangleWithACross() {
		assertEquals(""
			+ "quarter: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 3; moveTo 107.53333 20.46; lineTo 101.933334 20.46; lineTo 104.73333 15.610258; lineTo 107.53333 20.46; closePath; setLineWidth 1.0; initPath 1; moveTo 102.75 10.379999; lineTo 107.37 10.379999; moveTo 105.06 12.69; lineTo 105.06 8.07; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)\n"
			+ "half: setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 1.0; initPath 1; moveTo 107.53333 20.46; lineTo 101.933334 20.46; lineTo 104.73333 15.610258; lineTo 107.53333 20.46; closePath; setLineWidth 1.0; initPath 1; moveTo 102.75 10.379999; lineTo 107.37 10.379999; moveTo 105.06 12.69; lineTo 105.06 8.07; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setForeground rgb(0,0,0); setBackground rgb(0,0,0); setFont font(0.0)"
			, drum(80));
		assertEquals(paint(80, TGDuration.QUARTER, -1), paint(80, TGDuration.QUARTER, 80));
	}
}
