package app.tuxguitar.editor.action.track;

import static app.tuxguitar.editor.action.SongActionFixture.reads;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins TGSetTrackStringCountAction: the caret track takes the string count, losing the notes on the strings it
 * drops. Without a count or without a caret track nothing changes.
 *
 * The six string track holds one beat, with notes on strings 1 and 6; it reads back as "string count / notes".
 */
public class TestSetTrackStringCountAction {

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGTrack track = fixture.song.getTrack(0);
	private final TGBeat beat = fixture.beat(track.getMeasure(0), 0);

	public TestSetTrackStringCountAction() {
		fixture.note(beat, 1, 3);
		fixture.note(beat, 6, 5);
		fixture.caret(beat, 1);
	}

	private String setCount(Integer count) {
		fixture.actionContext.setAttribute(TGSetTrackStringCountAction.ATTRIBUTE_STRING_COUNT, count);
		fixture.execute(new TGSetTrackStringCountAction(fixture.context));
		return track.stringCount() + " / " + SongActionFixture.describe(beat);
	}

	@TestFactory
	public List<DynamicTest> settingTheStringCount() {
		return Arrays.asList(
			reads("fewer strings drop the notes on the strings beyond", "4 / s1f3", () -> new TestSetTrackStringCountAction().setCount(4)),
			reads("without a count nothing changes", "6 / s1f3 s6f5", () -> new TestSetTrackStringCountAction().setCount(null)),
			reads("without caret track nothing changes", "6 / s1f3 s6f5", () -> {
				TestSetTrackStringCountAction test = new TestSetTrackStringCountAction();
				test.fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, null);
				return test.setCount(4);
			}));
	}
}
