package app.tuxguitar.editor.action.note;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins TGSetNoteFretNumberAction, typing a fret digit: it sets a note of that fret on the caret string of the caret
 * beat. A digit typed within a second after another one on the same beat and string makes a two-digit fret, unless
 * that passes the track's last fret (29), which a percussion track has not. An action made for a number of two digits
 * (the app makes them for 0 to 9 only) sets that fret on the string of the last digit typed, and is not remembered.
 *
 * Two beats are read back as "first | second".
 */
public class TestSetNoteFretNumberAction {

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGTrack track = fixture.song.getTrack(0);
	private final TGMeasure measure = track.getMeasure(0);
	private final TGBeat first = fixture.beat(measure, 0);
	private final TGBeat second = fixture.beat(measure, 1);

	public TestSetNoteFretNumberAction() {
		// the action remembers the last digit typed in static fields: a 0 typed in another song keeps the previous
		// test's last digit from joining the first one typed here
		SongActionFixture other = new SongActionFixture();
		type(other, other.beat(other.song.getTrack(0).getMeasure(0), 0), 1, new TGSetNoteFretNumberAction(other.context, 0));
	}

	private static void type(SongActionFixture fixture, TGBeat beat, int string, TGSetNoteFretNumberAction action) {
		TGTrack track = beat.getMeasure().getTrack();
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, track.getString(string));
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, beat.getVoice(0));
		fixture.execute(action);
	}

	private String type(TGBeat beat, int string, int... digits) {
		for (int digit : digits) {
			type(fixture, beat, string, new TGSetNoteFretNumberAction(fixture.context, digit));
		}
		return fixture.outcome(first, second);
	}

	// a digit typed on string 3 of the first beat when the clock reads the given milliseconds
	private String typeAt(long time, int digit) {
		type(fixture, first, 3, new TGSetNoteFretNumberAction(fixture.context, digit) {
			@Override
			protected long currentTimeMillis() {
				return time;
			}
		});
		return fixture.outcome(first, second);
	}

	@Test
	public void aDigitSetsANoteOnTheCaretString() {
		assertEquals("s3f5 |  | success", type(first, 3, 5));
	}

	@Test
	public void aDigitReplacesTheNoteOnTheCaretString() {
		fixture.note(first, 3, 7);

		assertEquals("s3f0 |  | success", type(first, 3, 0));
	}

	@Test
	public void twoQuickDigitsMakeATwoDigitFret() {
		assertEquals("s3f12 |  | success", type(first, 3, 1, 2));
	}

	@Test
	public void theLastFretCanBeTyped() {
		assertEquals("s3f29 |  | success", type(first, 3, 2, 9));
	}

	@Test
	public void aDigitPassingTheLastFretStartsAFreshFret() {
		assertEquals("s3f0 |  | success", type(first, 3, 3, 0));
	}

	@Test
	public void aPercussionTrackHasNoLastFret() {
		fixture.song.getChannel(0).setBank(TGChannel.DEFAULT_PERCUSSION_BANK);

		assertEquals("s3f35 |  | success", type(first, 3, 3, 5));
	}

	@Test
	public void aThirdDigitStartsAFreshFret() {
		fixture.song.getChannel(0).setBank(TGChannel.DEFAULT_PERCUSSION_BANK);

		assertEquals("s3f5 |  | success", type(first, 3, 1, 0, 5));
	}

	@Test
	public void aDigitOnAnotherStringStartsAFreshFret() {
		type(first, 3, 1);

		assertEquals("s2f2 s3f1 |  | success", type(first, 2, 2));
	}

	@Test
	public void aDigitOnAnotherBeatStartsAFreshFret() {
		type(first, 3, 1);

		assertEquals("s3f1 | s3f2 | success", type(second, 3, 2));
	}

	@Test
	public void aDigitTypedWithinASecondJoinsTheLastOne() {
		typeAt(5000, 1);

		assertEquals("s3f12 |  | success", typeAt(5999, 2));
	}

	@Test
	public void aDigitTypedASecondLaterStartsAFreshFret() {
		typeAt(5000, 1);

		assertEquals("s3f2 |  | success", typeAt(6000, 2));
	}

	@Test
	public void aNumberOfTwoDigitsGoesOnTheStringOfTheLastDigitTyped() {
		type(first, 3, 5);

		assertEquals("s3f10 |  | success", type(first, 2, 10));
	}
}
