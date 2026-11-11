package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGNote;

/**
 * Pins the position lookups of TGMeasureManager in a 4/4 measure: the beat sounding at a tick position
 * (getBeatIn) and the next note on a string in a voice (getNextNote). Positions are in quarters from the
 * measure start; "-" means nothing was found.
 */
public class TestMeasureQueries {

	private static final long Q = MeasureFixture.QUARTER;

	private final MeasureFixture fixture = new MeasureFixture();

	private long ticksAt(double quarters) {
		return fixture.measure.getStart() + (long) (quarters * TGDuration.QUARTER_TIME);
	}

	private String beatsIn(double... positions) {
		StringBuilder found = new StringBuilder();
		for (double position : positions) {
			TGBeat beat = fixture.manager.getBeatIn(fixture.measure, ticksAt(position));
			found.append(found.length() == 0 ? "" : " ");
			found.append(beat == null ? "-" : String.valueOf((beat.getPreciseStart() - fixture.measure.getPreciseStart()) / Q));
		}
		return found.toString();
	}

	// fret of the next note after the beat at "position", or "-"
	private String nextNoteFret(double position, int voiceIndex, int string) {
		TGNote note = fixture.manager.getNextNote(fixture.measure, ticksAt(position), voiceIndex, string);
		return note == null ? "-" : String.valueOf(note.getValue());
	}

	@Test
	public void theBeatInAPositionIsTheOneStartedAndNotYetEnded() {
		fixture.quarterNotes(0, 1, 3);
		fixture.rest(2 * Q, TGDuration.QUARTER, 0);
		assertEquals("0 0 1 2 3 -", beatsIn(0, 0.5, 1, 2.5, 3.5, 4));
	}

	@Test
	public void aBeatEndsWithItsShortestVoiceAndTheLatestStartedBeatWins() {
		fixture.note(0, TGDuration.HALF, 0);
		fixture.note(Q, TGDuration.QUARTER, 1);
		fixture.note(2 * Q, TGDuration.HALF, 0);
		fixture.note(2 * Q, TGDuration.EIGHTH, 1);
		assertEquals("0 1 2 -", beatsIn(0.5, 1.5, 2.25, 2.75));
	}

	@Test
	public void theNextNoteIsTheFirstOnTheStringInTheVoiceAfterTheBeat() {
		fixture.fretted(0, 1, 0);
		fixture.fretted(Q, 2, 1);
		fixture.fretted(2 * Q, 3, 2, 1);
		fixture.fretted(2 * Q, 1, 22, 0);
		fixture.manager.removeVoice(fixture.beatAt(2 * Q).getVoice(0));
		fixture.fretted(3 * Q, 1, 3, 0);
		fixture.fretted(3 * Q, 1, 13, 1);

		String found = nextNoteFret(0, 0, 1) + " " + nextNoteFret(0, 0, 2) + " " + nextNoteFret(0, 0, 4) + " "
				+ nextNoteFret(0, 1, 1) + " " + nextNoteFret(3, 0, 1) + " " + nextNoteFret(0.5, 0, 1);

		// the removed voice 0 at quarter 2 still holds a note on string 1 at fret 22: it is skipped
		assertEquals("3 1 - 13 - -", found);
	}
}
