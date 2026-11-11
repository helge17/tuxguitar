package app.tuxguitar.editor.action.note;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins TGDeleteNoteOrRestAction, the delete key: it deletes the selected notes; without selected notes, the note on
 * the caret string of each selected beat; without selected beats, the note on the caret string of the caret beat. On a
 * rest it removes the caret voice, moving the next beats back and filling the end of the measure with a rest, or only
 * the text of a beat that has one. In free edition mode the last beat of the measure, when all rest, is removed whole.
 *
 * The measure is read back beat by beat as "<quarter>:<voice 0>", voice 0 being its notes, "rest" or "-" when empty,
 * followed by "T" when the beat has a text. It starts as "0:s2f1 s3f3 | 1:s2f2 s3f4 | 2:rest | 3:s2f6".
 */
public class TestDeleteNoteOrRestAction {

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGMeasure measure = fixture.song.getTrack(0).getMeasure(0);
	private final TGBeat first = fixture.beat(measure, 0);
	private final TGBeat second = fixture.beat(measure, 1);
	private final TGBeat third = fixture.beat(measure, 2);
	private final TGBeat fourth = fixture.beat(measure, 3);

	public TestDeleteNoteOrRestAction() {
		fixture.note(first, 2, 1);
		fixture.note(first, 3, 3);
		fixture.note(second, 2, 2);
		fixture.note(second, 3, 4);
		fixture.note(fourth, 2, 6);
		fixture.caret(first, 3);
	}

	private String delete() {
		fixture.execute(new TGDeleteNoteOrRestAction(fixture.context));
		List<String> beats = new ArrayList<String>();
		for (TGBeat beat : measure.getBeats()) {
			long quarter = (beat.getStart() - measure.getStart()) / TGDuration.QUARTER_TIME;
			beats.add(quarter + ":" + voice(beat.getVoice(0)) + (beat.isTextBeat() ? "T" : ""));
		}
		return String.join(" | ", beats);
	}

	private String deleteAt(TGBeat beat) {
		fixture.caret(beat, 3);
		return delete();
	}

	private static String voice(TGVoice voice) {
		if (voice.isEmpty()) {
			return "-";
		}
		return voice.isRestVoice() ? "rest" : SongActionFixture.describe(voice.getBeat());
	}

	// the beat, holding one note, made all rest in two voices
	private void restInTwoVoices(TGBeat beat) {
		beat.getVoice(0).removeNote(beat.getVoice(0).getNote(0));
		beat.getVoice(1).setEmpty(false);
	}

	@Test
	public void theSelectedNotesAreDeleted() {
		fixture.select(first.getVoice(0).getNote(1), second.getVoice(0).getNote(0));

		assertEquals("0:s2f1 | 1:s3f4 | 2:rest | 3:s2f6", delete());
	}

	@Test
	public void withoutSelectedNotesTheCaretStringIsClearedInTheSelectedBeats() {
		fixture.selectBeats(first, second);

		assertEquals("0:s2f1 | 1:s2f2 | 2:rest | 3:s2f6", deleteAt(fourth));
	}

	@Test
	public void anEmptyNoteSelectionFallsBackToTheSelectedBeats() {
		fixture.select();
		fixture.selectBeats(second);

		assertEquals("0:s2f1 s3f3 | 1:s2f2 | 2:rest | 3:s2f6", delete());
	}

	@Test
	public void withoutSelectionTheNoteOnTheCaretStringIsDeleted() {
		assertEquals("0:s2f1 | 1:s2f2 s3f4 | 2:rest | 3:s2f6", delete());
	}

	@Test
	public void anEmptyBeatSelectionFallsBackToTheCaret() {
		fixture.selectBeats();

		assertEquals("0:s2f1 | 1:s2f2 s3f4 | 2:rest | 3:s2f6", delete());
	}

	@Test
	public void withoutCaretBeatNothingIsDeleted() {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, null);

		assertEquals("0:s2f1 s3f3 | 1:s2f2 s3f4 | 2:rest | 3:s2f6", delete());
	}

	@Test
	public void aRestIsRemovedAndTheNextBeatsMoveBackLeavingARestAtTheEnd() {
		assertEquals("0:s2f1 s3f3 | 1:s2f2 s3f4 | 2:s2f6 | 3:rest", deleteAt(third));
	}

	@Test
	public void aRestWithATextOnlyLosesTheText() {
		third.setText(fixture.songManager.getFactory().newText());

		assertEquals("0:s2f1 s3f3 | 1:s2f2 s3f4 | 2:rest | 3:s2f6", deleteAt(third));
	}

	@Test
	public void outsideFreeEditionModeTheLastBeatAllRestStays() {
		restInTwoVoices(fourth);

		assertEquals("0:s2f1 s3f3 | 1:s2f2 s3f4 | 2:rest | 3:rest", deleteAt(fourth));
	}

	@Test
	public void inFreeEditionModeTheLastBeatAllRestIsRemovedWhole() {
		fixture.songManager.setFreeEditionMode(true);
		restInTwoVoices(fourth);

		assertEquals("0:s2f1 s3f3 | 1:s2f2 s3f4 | 2:rest", deleteAt(fourth));
	}

	@Test
	public void inFreeEditionModeARestBeforeTheLastBeatIsRemovedAsUsual() {
		fixture.songManager.setFreeEditionMode(true);

		assertEquals("0:s2f1 s3f3 | 1:s2f2 s3f4 | 2:s2f6 | 3:rest", deleteAt(third));
	}
}
