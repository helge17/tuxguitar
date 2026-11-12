package app.tuxguitar.editor.action.note;

import static app.tuxguitar.editor.action.SongActionFixture.reads;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGVelocities;
import app.tuxguitar.util.TGNoteRange;

/**
 * Pins TGChangeVelocityAction: the selected notes take the velocity and the action reports success; without a
 * velocity, or without selected notes, nothing changes and no success is reported.
 *
 * The first beat holds two notes and the second one; each note reads back as "s<string>f<fret>v<velocity>", the
 * beats separated by " | ", then "success" when the action reported it.
 */
public class TestChangeVelocityAction {

	private static final int LOUD = TGVelocities.FORTE_FORTISSIMO;
	private static final String UNCHANGED = "s1f1v" + TGVelocities.DEFAULT + " s2f2v" + TGVelocities.DEFAULT + " | s3f3v" + TGVelocities.DEFAULT + " | ";

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGMeasure measure = fixture.song.getTrack(0).getMeasure(0);
	private final TGBeat first = fixture.beat(measure, 0);
	private final TGBeat second = fixture.beat(measure, 1);

	public TestChangeVelocityAction() {
		fixture.select(fixture.note(first, 1, 1), fixture.note(second, 3, 3));
		fixture.note(first, 2, 2);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, LOUD);
	}

	private String changeVelocity() {
		fixture.execute(new TGChangeVelocityAction(fixture.context));
		return SongActionFixture.describe(first, TestChangeVelocityAction::velocity) + " | " + SongActionFixture.describe(second, TestChangeVelocityAction::velocity) + " | " + fixture.outcome();
	}

	private static String velocity(TGNote note) {
		return "v" + note.getVelocity();
	}

	@Test
	public void theSelectedNotesTakeTheVelocity() {
		assertEquals("s1f1v" + LOUD + " s2f2v" + TGVelocities.DEFAULT + " | s3f3v" + LOUD + " | success", changeVelocity());
	}

	@TestFactory
	public List<DynamicTest> nothingChanges() {
		return Arrays.asList(
			without("without velocity", TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, null),
			without("with an empty selection", TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE, TGNoteRange.empty()),
			without("without selection", TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE, null));
	}

	// a case run on a new song: the attribute set to the value, the notes keep their velocity
	private static DynamicTest without(String name, String attribute, Object value) {
		return reads(name, UNCHANGED, () -> {
			TestChangeVelocityAction test = new TestChangeVelocityAction();
			test.fixture.actionContext.setAttribute(attribute, value);
			return test.changeVelocity();
		});
	}
}
