package app.tuxguitar.editor.action.note;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;

/**
 * Pins TGMoveBeatsAction: the caret track beats from the caret beat on move by the precise move; a backward move
 * first drops the beats it moves over. Without a caret beat, measure, track or move, nothing moves.
 *
 * The first measure holds three beats, on quarters 0, 1 and 2, with notes of frets 0, 1 and 2, and the caret is on
 * the second; the measure reads back as its beats, each "quarter:notes".
 */
public class TestMoveBeatsAction {

	private static final String UNCHANGED = "0:s1f0 1:s1f1 2:s1f2";
	private static final long QUARTER = TGDuration.toPreciseTime(TGDuration.QUARTER_TIME);

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGMeasure measure = fixture.song.getTrack(0).getMeasure(0);

	public TestMoveBeatsAction() {
		for (int quarter = 0; quarter < 3; quarter++) {
			fixture.note(fixture.beat(measure, quarter), 1, quarter);
		}
		fixture.caret(measure.getBeat(1), 1);
	}

	private String move(Long preciseMove) {
		fixture.actionContext.setAttribute(TGMoveBeatsAction.ATTRIBUTE_PRECISE_MOVE, preciseMove);
		fixture.execute(new TGMoveBeatsAction(fixture.context));
		List<String> beats = new ArrayList<String>();
		for (TGBeat beat : measure.getBeats()) {
			beats.add(((beat.getStart() - measure.getStart()) / TGDuration.QUARTER_TIME) + ":" + SongActionFixture.describe(beat));
		}
		return String.join(" ", beats);
	}

	@Test
	public void theBeatsFromTheCaretMoveForward() {
		assertEquals("0:s1f0 2:s1f1 3:s1f2", move(QUARTER));
	}

	@Test
	public void aBackwardMoveDropsTheBeatsItMovesOver() {
		assertEquals("0:s1f0 1:s1f2", move(-QUARTER));
	}

	@TestFactory
	public List<DynamicTest> nothingMovesWithoutTheCaret() {
		return Arrays.asList(
			withoutCaret(TGDocumentContextAttributes.ATTRIBUTE_BEAT),
			withoutCaret(TGDocumentContextAttributes.ATTRIBUTE_MEASURE),
			withoutCaret(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
	}

	// a case run on a new song: with the caret attribute removed, a forward move leaves the beats in place
	private static DynamicTest withoutCaret(String attribute) {
		return DynamicTest.dynamicTest("without " + attribute, () -> {
			TestMoveBeatsAction test = new TestMoveBeatsAction();
			test.fixture.actionContext.setAttribute(attribute, null);
			assertEquals(UNCHANGED, test.move(QUARTER));
		});
	}

	@Test
	public void nothingMovesWithoutAMove() {
		assertEquals(UNCHANGED, move(null));
	}
}
