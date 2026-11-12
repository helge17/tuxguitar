package app.tuxguitar.editor.action.duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.util.TGBeatRange;

/**
 * Pins TGSetDurationAction: the selected beats take the duration in the caret voice; without a selection (or with an
 * empty one) the caret beat does, and without a caret beat nothing changes. A shortened beat is followed by a rest.
 *
 * The first measure is filled with four quarter beats, each holding one note, and the caret is on the second; the
 * measure reads back as its beats, each the duration value of voice 0, "r" for a rest.
 */
public class TestSetDurationAction {

	private static final String UNCHANGED = "4 4 4 4";

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGMeasure measure = fixture.song.getTrack(0).getMeasure(0);

	public TestSetDurationAction() {
		for (int quarter = 0; quarter < 4; quarter++) {
			fixture.note(fixture.beat(measure, quarter), 1, quarter);
		}
		fixture.caret(measure.getBeat(1), 1);
		TGDuration eighth = fixture.songManager.getFactory().newDuration();
		eighth.setValue(TGDuration.EIGHTH);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION, eighth);
	}

	private String setDuration() {
		fixture.execute(new TGSetDurationAction(fixture.context));
		List<String> beats = new ArrayList<String>();
		for (TGBeat beat : measure.getBeats()) {
			beats.add(durationOf(beat));
		}
		return String.join(" ", beats);
	}

	private static String durationOf(TGBeat beat) {
		return beat.getVoice(0).getDuration().getValue() + (beat.isRestBeat() ? "r" : "");
	}

	@Test
	public void theSelectedBeatsTakeTheDuration() {
		fixture.selectBeats(measure.getBeat(0), measure.getBeat(2));

		assertEquals("8 8r 4 8 8r 4", setDuration());
	}

	@Test
	public void withoutSelectionTheCaretBeatTakesTheDuration() {
		assertEquals("4 8 8r 4 4", setDuration());
	}

	@Test
	public void withAnEmptySelectionTheCaretBeatTakesTheDuration() {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE, TGBeatRange.empty());

		assertEquals("4 8 8r 4 4", setDuration());
	}

	@Test
	public void theDurationGoesInTheCaretVoice() {
		TGBeat caretBeat = measure.getBeat(1);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, caretBeat.getVoice(1));

		setDuration();

		assertEquals("4 / 8", caretBeat.getVoice(0).getDuration().getValue() + " / " + caretBeat.getVoice(1).getDuration().getValue());
	}

	@Test
	public void withoutCaretBeatNothingChanges() {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, null);

		assertEquals(UNCHANGED, setDuration());
	}
}
