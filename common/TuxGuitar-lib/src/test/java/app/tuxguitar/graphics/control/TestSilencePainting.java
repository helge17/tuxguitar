package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.ui.resource.UIColorModel;

/**
 * Pins how a rest paints itself (TGVoiceImpl.paint of a rest voice, TGVoiceImpl.paintSilence): the symbol of its
 * duration, placed on the score when it is shown, else on the tablature, scaled to the staff; then one or two dots
 * in the note color of that staff; in play mode, the played rest in the play color.
 *
 * The rest starts an otherwise empty 4/4 measure; once printed, it is painted alone at (0, 0) and described as
 * "color first-point xN" (N drawing calls), followed by its dots. Score notes are blue, tablature notes green,
 * the played note red.
 */
public class TestSilencePainting {

	private static final int[] DURATIONS = {
		TGDuration.WHOLE, TGDuration.HALF, TGDuration.QUARTER, TGDuration.EIGHTH,
		TGDuration.SIXTEENTH, TGDuration.THIRTY_SECOND, TGDuration.SIXTY_FOURTH,
	};

	private LayoutHarness harness;

	@Test
	public void eachDurationHasItsSymbolOnTheScore() {
		assertEquals(""
			+ "1: rgb(0,0,255) 81.03641 27.03641 x21\n"
			+ "2: rgb(0,0,255) 81.03641 31.196465 x21\n"
			+ "4: rgb(0,0,255) 82.63599 27.814543 x37\n"
			+ "8: rgb(0,0,255) 82.30511 29.388477 x26\n"
			+ "16: rgb(0,0,255) 83.738884 27.832924 x38\n"
			+ "32: rgb(0,0,255) 84.84181 24.721811 x50\n"
			+ "64: rgb(0,0,255) 85.59545 23.166256 x62\n"
			, restByDuration(SCORE | TAB));
	}

	@Test
	public void withoutTheScoreTheSymbolIsOnTheTablature() {
		assertEquals(""
			+ "1: rgb(0,255,0) 44.03745 36.03745 x21\n"
			+ "2: rgb(0,255,0) 44.03745 41.11636 x21\n"
			+ "4: rgb(0,255,0) 45.682728 33.637814 x37\n"
			+ "8: rgb(0,255,0) 45.3424 35.25672 x26\n"
			+ "16: rgb(0,255,0) 46.81714 33.65672 x38\n"
			+ "32: rgb(0,255,0) 47.95158 30.45672 x50\n"
			+ "64: rgb(0,255,0) 48.72675 28.85672 x62\n"
			, restByDuration(TAB));
	}

	@Test
	public void aDottedRestHasOneDotAndADoubleDottedRestTwo() {
		assertEquals(""
			+ "rgb(0,0,255) 82.63599 27.814543 x44, dots in rgb(0,0,255): addCircle 91.0 29.777779 3.0\n"
			+ "rgb(0,255,0) 45.682728 33.637814 x46, dots in rgb(0,255,0): addCircle 54.0 35.6 3.0 addCircle 59.0 35.6 3.0"
			, rest(SCORE | TAB, TGDuration.QUARTER, true, false) + "\n" + rest(TAB, TGDuration.QUARTER, false, true));
	}

	@Test
	public void inPlayModeThePlayedRestIsInThePlayColor() {
		harness = harness();
		TGVoiceImpl rest = harness.rest(0, TGDuration.QUARTER, 0);
		harness.playMode = true;
		TGLayout layout = harness.screen(TAB);

		assertEquals("rgb(255,0,0) 45.682728 33.637814 x37", paintAlone(rest, layout));
	}

	// ---- helpers ----

	private LayoutHarness harness() {
		LayoutHarness harness = new LayoutHarness();
		harness.styles.setScoreNoteColor(new UIColorModel(0, 0, 255));
		harness.styles.setTabNoteColor(new UIColorModel(0, 255, 0));
		harness.styles.setPlayNoteColor(new UIColorModel(255, 0, 0));
		return harness;
	}

	private String restByDuration(int style) {
		StringBuilder text = new StringBuilder();
		for (int duration : DURATIONS) {
			text.append(duration).append(": ").append(rest(style, duration, false, false)).append('\n');
		}
		return text.toString();
	}

	private String rest(int style, int durationValue, boolean dotted, boolean doubleDotted) {
		harness = harness();
		TGVoiceImpl rest = harness.rest(0, durationValue, 0);
		rest.getDuration().setDotted(dotted);
		rest.getDuration().setDoubleDotted(doubleDotted);
		harness.print(style);
		return paintAlone(rest, harness.layout());
	}

	private String paintAlone(TGVoiceImpl rest, TGLayout layout) {
		harness.painter.clear();
		rest.paint(layout, harness.painter, 0, 0);
		List<String> calls = harness.painter.calls();
		String description = harness.painter.callsStartingWith("setForeground").get(0).substring("setForeground ".length())
			+ " " + harness.painter.callsStartingWith("moveTo").get(0).substring("moveTo ".length())
			+ " x" + calls.size();
		List<String> dots = harness.painter.callsStartingWith("addCircle");
		if (!dots.isEmpty()) {
			List<String> colors = harness.painter.callsStartingWith("setForeground");
			description += ", dots in " + colors.get(colors.size() - 1).substring("setForeground ".length()) + ": " + String.join(" ", dots);
		}
		return description;
	}
}
