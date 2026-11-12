package app.tuxguitar.editor.action.duration;

import static app.tuxguitar.editor.action.SongActionFixture.reads;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.util.TGContext;

/**
 * Pins the seven fixed duration actions, whole to sixty-fourth: each makes the duration to set its own value,
 * undotted, and sets it on the caret beat, unless the duration to set has that value already and the caret voice is
 * empty or has it too.
 *
 * The first measure holds one quarter beat with a note, under the caret; the duration to set starts dotted. It reads
 * back as "beat duration / duration to set", a dotted duration ending in ".".
 */
public class TestSetFixedDurationAction {

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGBeat beat = fixture.beat(fixture.song.getTrack(0).getMeasure(0), 0);
	private final TGDuration duration = fixture.songManager.getFactory().newDuration();

	public TestSetFixedDurationAction() {
		fixture.note(beat, 1, 0);
		fixture.caret(beat, 1);
		duration.setDotted(true);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION, duration);
		fixture.map(new TGSetDurationAction(fixture.context));
	}

	private String set(Function<TGContext, TGActionBase> action, int durationValue) {
		duration.setValue(durationValue);
		fixture.execute(action.apply(fixture.context));
		return read(beat.getVoice(0).getDuration()) + " / " + read(duration);
	}

	private static String read(TGDuration duration) {
		return duration.getValue() + (duration.isDotted() ? "." : "");
	}

	@TestFactory
	public List<DynamicTest> eachActionSetsItsValue() {
		return Arrays.asList(
			setting("whole", "1 / 1", TGSetWholeDurationAction::new, TGDuration.QUARTER),
			setting("half", "2 / 2", TGSetHalfDurationAction::new, TGDuration.QUARTER),
			setting("quarter", "4 / 4", TGSetQuarterDurationAction::new, TGDuration.EIGHTH),
			setting("eighth", "8 / 8", TGSetEighthDurationAction::new, TGDuration.QUARTER),
			setting("sixteenth", "16 / 16", TGSetSixteenthDurationAction::new, TGDuration.QUARTER),
			setting("thirty-second", "32 / 32", TGSetThirtySecondDurationAction::new, TGDuration.QUARTER),
			setting("sixty-fourth", "64 / 64", TGSetSixtyFourthDurationAction::new, TGDuration.QUARTER));
	}

	@TestFactory
	public List<DynamicTest> whenTheDurationToSetHasTheValueAlready() {
		return Arrays.asList(
			setting("a caret voice of another duration still takes it", "8 / 8", TGSetEighthDurationAction::new, TGDuration.EIGHTH),
			setting("a caret voice of that duration leaves all as is", "4 / 4.", TGSetQuarterDurationAction::new, TGDuration.QUARTER),
			reads("an empty caret voice leaves all as is", "4 / 8.", () -> {
				TestSetFixedDurationAction test = new TestSetFixedDurationAction();
				test.fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, test.beat.getVoice(1));
				return test.set(TGSetEighthDurationAction::new, TGDuration.EIGHTH);
			}));
	}

	private static DynamicTest setting(String name, String expected, Function<TGContext, TGActionBase> action, int durationValue) {
		return reads(name, expected, () -> new TestSetFixedDurationAction().set(action, durationValue));
	}
}
