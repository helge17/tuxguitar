package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins two clean-ups of a 4/4 measure in TGMeasureManager: dropping a voice that holds only rests
 * (removeMeasureVoices) and dropping the notes above a fret (removeNotesAfterFret).
 * Positions are in quarters from the measure start.
 */
public class TestRemoveFromMeasure {

	private static final long Q = MeasureFixture.QUARTER;

	private final MeasureFixture fixture = new MeasureFixture();

	private List<TGBeat> beatsInOrder() {
		List<TGBeat> beats = new ArrayList<TGBeat>(fixture.measure.getBeats());
		beats.sort(Comparator.comparingLong(TGBeat::getPreciseStart));
		return beats;
	}

	private long position(TGBeat beat) {
		return (beat.getPreciseStart() - fixture.measure.getPreciseStart()) / Q;
	}

	private static String kind(TGVoice voice) {
		return voice.isEmpty() ? "_" : (voice.isRestVoice() ? "r" : "n");
	}

	// "position:<kind of voice 0><kind of voice 1>" per beat; _ = empty, r = rest, n = note
	private String voices() {
		StringBuilder layout = new StringBuilder();
		for (TGBeat beat : beatsInOrder()) {
			layout.append(layout.length() == 0 ? "" : " ");
			layout.append(position(beat)).append(':').append(kind(beat.getVoice(0))).append(kind(beat.getVoice(1)));
		}
		return layout.toString();
	}

	// "position v<voice> s<string>f<fret>" per note
	private String notes() {
		StringBuilder notes = new StringBuilder();
		for (TGBeat beat : beatsInOrder()) {
			for (int v = 0; v < beat.countVoices(); v++) {
				for (TGNote note : beat.getVoice(v).getNotes()) {
					notes.append(notes.length() == 0 ? "" : ", ");
					notes.append(position(beat)).append(" v").append(v).append(" s").append(note.getString()).append('f').append(note.getValue());
				}
			}
		}
		return notes.toString();
	}

	@Test
	public void aVoiceIsDroppedOnlyWhenItHoldsNothingButRests() {
		fixture.rest(0, TGDuration.HALF, 0);
		fixture.note(2 * Q, TGDuration.HALF, 0);
		for (int quarter = 0; quarter < 4; quarter++) {
			fixture.rest(quarter * Q, TGDuration.QUARTER, 1);
		}

		fixture.manager.removeMeasureVoices(fixture.measure, 0);
		fixture.manager.removeMeasureVoices(fixture.measure, 1);

		// voice 0 holds a note and is kept; the beats left with no voice at quarters 1 and 3 are gone
		assertEquals("0:r_ 2:n_", voices());
	}

	@Test
	public void notesAboveTheFretAreRemovedFromEveryVoice() {
		fixture.fretted(0, 1, 3);
		fixture.fretted(0, 2, 5);
		fixture.fretted(0, 3, 7);
		fixture.fretted(Q, 1, 9, 1);
		fixture.fretted(Q, 2, 5, 1);

		fixture.manager.removeNotesAfterFret(fixture.measure, 5);

		assertEquals("0 v0 s1f3, 0 v0 s2f5, 1 v1 s2f5", notes());
	}
}
