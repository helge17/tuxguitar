package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.song.models.effects.TGEffectHarmonic;
import app.tuxguitar.song.models.effects.TGEffectTrill;

/**
 * Pins how a note draws its "offline" effects, the ones stacked above the tablature and hidden in play
 * mode (TGNoteImpl.paintOfflineEffects): symbols for accents, fade in, vibrato and trill, text labels for
 * harmonics, tapping, slapping, popping, palm mute and let ring.
 *
 * The note is a quarter note, fret 5 on string 3, at the start of a 4/4 measure followed by quarter notes.
 * Once the measure is printed, the note's offline effects are painted alone at (0, 0), one call per line.
 */
public class TestOfflineEffectsPainting {

	private final LayoutHarness harness = new LayoutHarness();
	private final TGNoteImpl note;

	public TestOfflineEffectsPainting() {
		harness.quarterNotes(1, 2, 3);
		note = harness.note(0, TGDuration.QUARTER, 0, 3, 5);
	}

	@Test
	public void aNoteWithoutEffectsOnlySetsTheEffectStyle() {
		assertEquals(""
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)"
			, paint(TAB, effect -> { }));
	}

	@Test
	public void textEffectsAreLabelledAboveTheTablature() {
		assertEquals(""
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "drawString 'T' 45.0 8.0\n"
			+ "--\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "drawString 'S' 45.0 8.0\n"
			+ "--\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "drawString 'P' 45.0 8.0\n"
			+ "--\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "drawString 'P.M' 45.0 8.0\n"
			+ "--\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "drawString 'L.R' 45.0 8.0"
			, paint(TAB, effect -> effect.setTapping(true))
			+ "\n--\n" + paint(TAB, effect -> effect.setSlapping(true))
			+ "\n--\n" + paint(TAB, effect -> effect.setPopping(true))
			+ "\n--\n" + paint(TAB, effect -> effect.setPalmMute(true))
			+ "\n--\n" + paint(TAB, effect -> effect.setLetRing(true)));
	}

	@Test
	public void textEffectsAreStackedInTheirOwnRowsAndLetRingReplacesPalmMute() {
		assertEquals(""
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "drawString 'T' 45.0 4.0\n"
			+ "drawString 'L.R' 45.0 12.0"
			, paint(TAB, effect -> {
				effect.setPalmMute(true);
				effect.setLetRing(true);
				effect.setTapping(true);
			}));
	}

	@Test
	public void aHarmonicIsLabelledByItsTypeOnTheTablatureOnly() {
		StringBuilder labels = new StringBuilder();
		for (int type = TGEffectHarmonic.TYPE_NATURAL; type <= TGEffectHarmonic.TYPE_SEMI; type++) {
			labels.append(harmonic(TAB, type)).append('\n');
		}
		labels.append("with score: ").append(harmonic(SCORE | TAB, TGEffectHarmonic.TYPE_NATURAL));

		assertEquals(""
			+ "setForeground rgb(0,0,0) setBackground rgb(0,0,0) setFont font(0.0) drawString 'N.H' 45.0 8.0\n"
			+ "setForeground rgb(0,0,0) setBackground rgb(0,0,0) setFont font(0.0) drawString 'A.H' 45.0 8.0\n"
			+ "setForeground rgb(0,0,0) setBackground rgb(0,0,0) setFont font(0.0) drawString 'T.H' 45.0 8.0\n"
			+ "setForeground rgb(0,0,0) setBackground rgb(0,0,0) setFont font(0.0) drawString 'P.H' 45.0 8.0\n"
			+ "setForeground rgb(0,0,0) setBackground rgb(0,0,0) setFont font(0.0) drawString 'S.H' 45.0 8.0\n"
			+ "with score: setForeground rgb(0,0,0) setBackground rgb(0,0,0) setFont font(0.0)"
			, labels.toString());
	}

	@Test
	public void accentsAndFadeInAreSymbols() {
		assertEquals(""
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "moveTo 45.0 6.0\n"
			+ "lineTo 51.0 8.5\n"
			+ "moveTo 51.0 8.5\n"
			+ "lineTo 45.0 11.0\n"
			+ "closePath\n"
			+ "--\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "moveTo 45.0 9.0\n"
			+ "lineTo 48.0 5.0\n"
			+ "moveTo 48.0 5.0\n"
			+ "lineTo 51.0 9.0\n"
			+ "closePath\n"
			+ "--\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "moveTo 45.0 8.0\n"
			+ "cubicTo 45.0 8.0 65.0 8.0 65.0 4.0\n"
			+ "moveTo 45.0 8.0\n"
			+ "cubicTo 45.0 8.0 65.0 8.0 65.0 12.0\n"
			+ "moveTo 65.0 12.0\n"
			+ "closePath"
			, paint(TAB, effect -> effect.setAccentuatedNote(true))
			+ "\n--\n" + paint(TAB, effect -> effect.setHeavyAccentuatedNote(true))
			+ "\n--\n" + paint(TAB, effect -> effect.setFadeIn(true)));
	}

	@Test
	public void vibratoAndTrillAreWavyLines() {
		assertEquals(""
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 1.0\n"
			+ "initPath 2\n"
			+ "moveTo 47.0 7.0\n"
			+ "lineTo 47.0 7.0\n"
			+ "cubicTo 47.0 7.0 48.0 6.0 49.0 7.0\n"
			+ "lineTo 51.0 9.0\n"
			+ "lineTo 53.0 7.0\n"
			+ "cubicTo 53.0 7.0 54.0 6.0 55.0 7.0\n"
			+ "lineTo 57.0 9.0\n"
			+ "lineTo 59.0 7.0\n"
			+ "cubicTo 59.0 7.0 60.0 6.0 61.0 7.0\n"
			+ "lineTo 63.0 9.0\n"
			+ "lineTo 64.0 8.0\n"
			+ "cubicTo 64.0 8.0 65.0 8.0 64.0 9.0\n"
			+ "lineTo 63.0 10.0\n"
			+ "cubicTo 63.0 10.0 62.0 11.0 61.0 10.0\n"
			+ "lineTo 59.0 8.0\n"
			+ "lineTo 58.0 9.0\n"
			+ "lineTo 57.0 10.0\n"
			+ "cubicTo 57.0 10.0 56.0 11.0 55.0 10.0\n"
			+ "lineTo 53.0 8.0\n"
			+ "lineTo 52.0 9.0\n"
			+ "lineTo 51.0 10.0\n"
			+ "cubicTo 51.0 10.0 50.0 11.0 49.0 10.0\n"
			+ "lineTo 47.0 8.0\n"
			+ "lineTo 46.0 9.0\n"
			+ "cubicTo 46.0 9.0 45.0 9.0 46.0 8.0\n"
			+ "lineTo 47.0 7.0\n"
			+ "closePath\n"
			+ "--\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "drawString 'Tr' 45.0 8.0\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 1.0\n"
			+ "initPath 2\n"
			+ "moveTo 55.0 9.0\n"
			+ "lineTo 55.0 9.0\n"
			+ "cubicTo 55.0 9.0 56.0 8.0 57.0 9.0\n"
			+ "lineTo 59.0 11.0\n"
			+ "lineTo 61.0 9.0\n"
			+ "cubicTo 61.0 9.0 62.0 8.0 63.0 9.0\n"
			+ "lineTo 65.0 11.0\n"
			+ "lineTo 66.0 10.0\n"
			+ "cubicTo 66.0 10.0 67.0 10.0 66.0 11.0\n"
			+ "lineTo 65.0 12.0\n"
			+ "cubicTo 65.0 12.0 64.0 13.0 63.0 12.0\n"
			+ "lineTo 61.0 10.0\n"
			+ "lineTo 60.0 11.0\n"
			+ "lineTo 59.0 12.0\n"
			+ "cubicTo 59.0 12.0 58.0 13.0 57.0 12.0\n"
			+ "lineTo 55.0 10.0\n"
			+ "lineTo 54.0 11.0\n"
			+ "cubicTo 54.0 11.0 53.0 11.0 54.0 10.0\n"
			+ "lineTo 55.0 9.0\n"
			+ "closePath"
			, paint(TAB, effect -> effect.setVibrato(true))
			+ "\n--\n" + paint(TAB, effect -> {
				TGEffectTrill trill = harness.factory.newEffectTrill();
				trill.setFret(7);
				effect.setTrill(trill);
			}));
	}

	@Test
	public void effectsAreStackedFromTopToBottomInAFixedOrder() {
		assertEquals(""
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "moveTo 45.0 2.0\n"
			+ "lineTo 51.0 4.5\n"
			+ "moveTo 51.0 4.5\n"
			+ "lineTo 45.0 7.0\n"
			+ "closePath\n"
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "moveTo 45.0 44.0\n"
			+ "cubicTo 45.0 44.0 65.0 44.0 65.0 40.0\n"
			+ "moveTo 45.0 44.0\n"
			+ "cubicTo 45.0 44.0 65.0 44.0 65.0 48.0\n"
			+ "moveTo 65.0 48.0\n"
			+ "closePath\n"
			+ "drawString 'P.H' 45.0 12.0\n"
			+ "drawString 'T' 45.0 20.0\n"
			+ "drawString 'L.R' 45.0 28.0\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 1.0\n"
			+ "initPath 2\n"
			+ "moveTo 47.0 35.0\n"
			+ "lineTo 47.0 35.0\n"
			+ "cubicTo 47.0 35.0 48.0 34.0 49.0 35.0\n"
			+ "lineTo 51.0 37.0\n"
			+ "lineTo 53.0 35.0\n"
			+ "cubicTo 53.0 35.0 54.0 34.0 55.0 35.0\n"
			+ "lineTo 57.0 37.0\n"
			+ "lineTo 59.0 35.0\n"
			+ "cubicTo 59.0 35.0 60.0 34.0 61.0 35.0\n"
			+ "lineTo 63.0 37.0\n"
			+ "lineTo 64.0 36.0\n"
			+ "cubicTo 64.0 36.0 65.0 36.0 64.0 37.0\n"
			+ "lineTo 63.0 38.0\n"
			+ "cubicTo 63.0 38.0 62.0 39.0 61.0 38.0\n"
			+ "lineTo 59.0 36.0\n"
			+ "lineTo 58.0 37.0\n"
			+ "lineTo 57.0 38.0\n"
			+ "cubicTo 57.0 38.0 56.0 39.0 55.0 38.0\n"
			+ "lineTo 53.0 36.0\n"
			+ "lineTo 52.0 37.0\n"
			+ "lineTo 51.0 38.0\n"
			+ "cubicTo 51.0 38.0 50.0 39.0 49.0 38.0\n"
			+ "lineTo 47.0 36.0\n"
			+ "lineTo 46.0 37.0\n"
			+ "cubicTo 46.0 37.0 45.0 37.0 46.0 36.0\n"
			+ "lineTo 47.0 35.0\n"
			+ "closePath"
			, paint(TAB, effect -> {
				effect.setAccentuatedNote(true);
				effect.setFadeIn(true);
				effect.setHarmonic(harmonicOfType(TGEffectHarmonic.TYPE_PINCH));
				effect.setTapping(true);
				effect.setPalmMute(true);
				effect.setLetRing(true);
				effect.setVibrato(true);
			}));
	}

	// ---- helpers ----

	private String harmonic(int style, int type) {
		return paint(style, effect -> effect.setHarmonic(harmonicOfType(type))).replace('\n', ' ');
	}

	private TGEffectHarmonic harmonicOfType(int type) {
		TGEffectHarmonic harmonic = harness.factory.newEffectHarmonic();
		harmonic.setType(type);
		return harmonic;
	}

	// sets the note's effects, prints the measure, then paints the note's offline effects alone
	private String paint(int style, Consumer<TGNoteEffect> effects) {
		effects.accept(note.getEffect());
		harness.print(style);
		harness.painter.clear();
		TGLayout layout = harness.layout();
		note.paintOfflineEffects(layout, harness.painter, 0, 0, note.getBeatImpl().getSpacing(layout));
		note.setEffect(harness.factory.newEffect());
		return String.join("\n", harness.painter.calls());
	}
}
