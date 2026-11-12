package app.tuxguitar.editor.action.note;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.util.TGBeatRange;

/**
 * Pins TGCleanBeatAction: the selected beats lose their notes; without a selection (or with an empty one) the caret
 * beat does, and without a caret beat nothing changes.
 *
 * Three beats each hold one note and the caret is on the second; they read back as "first | second | third".
 */
public class TestCleanBeatAction {

	private static final String UNCHANGED = "s1f1 | s2f2 | s3f3";

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGMeasure measure = fixture.song.getTrack(0).getMeasure(0);
	private final TGBeat first = fixture.beat(measure, 0);
	private final TGBeat second = fixture.beat(measure, 1);
	private final TGBeat third = fixture.beat(measure, 2);

	public TestCleanBeatAction() {
		fixture.note(first, 1, 1);
		fixture.note(second, 2, 2);
		fixture.note(third, 3, 3);
		fixture.caret(second, 2);
	}

	private String clean() {
		fixture.execute(new TGCleanBeatAction(fixture.context));
		return fixture.outcome(first, second, third);
	}

	@Test
	public void theSelectedBeatsAreCleaned() {
		fixture.selectBeats(first, third);

		assertEquals(" | s2f2 | ", clean());
	}

	@Test
	public void withoutSelectionTheCaretBeatIsCleaned() {
		assertEquals("s1f1 |  | s3f3", clean());
	}

	@Test
	public void withAnEmptySelectionTheCaretBeatIsCleaned() {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE, TGBeatRange.empty());

		assertEquals("s1f1 |  | s3f3", clean());
	}

	@Test
	public void withoutCaretBeatNothingChanges() {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, null);

		assertEquals(UNCHANGED, clean());
	}
}
