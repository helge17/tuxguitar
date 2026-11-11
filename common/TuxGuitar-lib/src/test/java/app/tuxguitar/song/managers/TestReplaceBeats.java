package app.tuxguitar.song.managers;

import static app.tuxguitar.song.managers.TrackDescription.QUARTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins how pasted beats replace the beats of a track in place (TGTrackManager.replaceBeats): from the given
 * position, beats are cleared until the pasted beat fits, the first of them becomes the pasted beat, the measures
 * after it are tidied, and the next pasted beat goes on the first beat after it; pasting stops when there is no room.
 *
 * The track holds 2 measures of 4 quarter notes, frets 1 to 8; pasted notes have frets from 20 on.
 * A result is the track as in TrackDescription (voices 0 and 1), then the pasted beats returned, as
 * "<measure>@<position in quarters>".
 */
public class TestReplaceBeats {

	private final TGSongManager songManager = new TGSongManager();
	private final TGFactory factory = songManager.getFactory();
	private final TGSong song = songManager.newSong();
	private final TGTrack track = song.getTrack(0);
	private final List<TGBeat> pasted = new ArrayList<TGBeat>();
	private int nextFret = 20;

	public TestReplaceBeats() {
		songManager.addNewMeasureBeforeEnd(song);
		int fret = 1;
		for (int m = 0; m < 2; m++) {
			TGMeasure measure = track.getMeasure(m);
			for (int q = 0; q < 4; q++) {
				TGBeat beat = factory.newBeat();
				beat.setPreciseStart(measure.getPreciseStart() + q * QUARTER);
				addNote(beat.getVoice(0), TGDuration.QUARTER, fret++);
				measure.addBeat(beat);
			}
		}
	}

	private void addNote(TGVoice voice, int durationValue, int fret) {
		voice.setEmpty(false);
		voice.getDuration().setValue(durationValue);
		TGNote note = factory.newNote();
		note.setString(1);
		note.setValue(fret);
		note.setVelocity(95);
		voice.addNote(note);
	}

	// a pasted beat of one note, in voice 0
	private TGVoice paste(int durationValue) {
		TGBeat beat = factory.newBeat();
		addNote(beat.getVoice(0), durationValue, nextFret++);
		pasted.add(beat);
		return beat.getVoice(0);
	}

	private String replaceAt(int measureIndex, long offset) {
		TGMeasure measure = track.getMeasure(measureIndex);
		List<TGBeat> updated = songManager.getTrackManager().replaceBeats(track, pasted, measure.getPreciseStart() + offset);
		StringBuilder description = new StringBuilder(TrackDescription.describe(track, 2)).append("returned:");
		for (TGBeat beat : updated) {
			double position = (beat.getPreciseStart() - beat.getMeasure().getPreciseStart()) / (double) QUARTER;
			description.append(' ').append(beat.getMeasure().getNumber()).append('@').append(position);
		}
		return description.toString();
	}

	@Test
	public void aQuarterReplacesAQuarter() {
		paste(TGDuration.QUARTER);
		assertEquals(""
			+ "1: n1@0/4|_@0/4 n20@1/4|_@1/4 n3@2/4|_@2/4 n4@3/4|_@3/4\n"
			+ "2: n5@0/4|_@0/4 n6@1/4|_@1/4 n7@2/4|_@2/4 n8@3/4|_@3/4\n"
			+ "returned: 1@1.0"
			, replaceAt(0, QUARTER));
	}

	@Test
	public void pastedBeatsReplaceTheBeatsInTurn() {
		paste(TGDuration.QUARTER);
		paste(TGDuration.QUARTER);
		assertEquals(""
			+ "1: n1@0/4|_@0/4 n20@1/4|_@1/4 n21@2/4|_@2/4 n4@3/4|_@3/4\n"
			+ "2: n5@0/4|_@0/4 n6@1/4|_@1/4 n7@2/4|_@2/4 n8@3/4|_@3/4\n"
			+ "returned: 1@1.0 1@2.0"
			, replaceAt(0, QUARTER));
	}

	@Test
	public void aHalfTakesTheRoomOfTwoQuarters() {
		paste(TGDuration.HALF);
		paste(TGDuration.QUARTER);
		assertEquals(""
			+ "1: n1@0/4|_@0/4 n20@1/2|_@1/4 n21@3/4|_@3/4\n"
			+ "2: n5@0/4|_@0/4 n6@1/4|_@1/4 n7@2/4|_@2/4 n8@3/4|_@3/4\n"
			+ "returned: 1@1.0 1@3.0"
			, replaceAt(0, QUARTER));
	}

	@Test
	public void anEighthLeavesARestBehind() {
		paste(TGDuration.EIGHTH);
		paste(TGDuration.QUARTER);
		assertEquals(""
			+ "1: n1@0/4|_@0/4 n20@1/8|_@1/4 n21@1.5/4|_@1.5/4 r@2.5/8|_@2.5/4 n4@3/4|_@3/4\n"
			+ "2: n5@0/4|_@0/4 n6@1/4|_@1/4 n7@2/4|_@2/4 n8@3/4|_@3/4\n"
			+ "returned: 1@1.0 1@1.5"
			, replaceAt(0, QUARTER));
	}

	@Test
	public void aBeatPastTheMeasureEndIsTiedIntoTheNext() {
		paste(TGDuration.HALF);
		paste(TGDuration.QUARTER);
		assertEquals(""
			+ "1: n1@0/4|_@0/4 n2@1/4|_@1/4 n3@2/4|_@2/4 n20@3/4|_@3/4\n"
			+ "2: n20~@0/4|_@0/4 n21@1/4|_@1/4 n7@2/4|_@2/4 n8@3/4|_@3/4\n"
			+ "returned: 1@3.0 2@1.0"
			, replaceAt(0, 3 * QUARTER));
	}

	@Test
	public void pastingStopsAtTheEndOfTheSong() {
		paste(TGDuration.HALF);
		paste(TGDuration.HALF);
		paste(TGDuration.QUARTER);
		assertEquals(""
			+ "1: n1@0/4|_@0/4 n2@1/4|_@1/4 n3@2/4|_@2/4 n4@3/4|_@3/4\n"
			+ "2: n20@0/2|_@0/4 n21@2/2|_@2/4\n"
			+ "returned: 2@0.0 2@2.0"
			, replaceAt(1, 0));
	}

	@Test
	public void aBeatWithoutRoomIsNotPastedButClearsTheRest() {
		paste(TGDuration.WHOLE);
		assertEquals(""
			+ "1: n1@0/4|_@0/4 n2@1/4|_@1/4 n3@2/4|_@2/4 n4@3/4|_@3/4\n"
			+ "2: n5@0/4|_@0/4 r@1/4|_@1/4 r@2/4|_@2/4 r@3/4|_@3/4\n"
			+ "returned:"
			, replaceAt(1, QUARTER));
	}

	@Test
	public void eachVoiceTakesItsOwnDuration() {
		TGVoice voice = paste(TGDuration.QUARTER);
		addNote(voice.getBeat().getVoice(1), TGDuration.HALF, 30);
		paste(TGDuration.QUARTER);
		assertEquals(""
			+ "1: n20@0/4|n30@0/2 n21@2/4|r@2/2 n4@3/4|_@3/4\n"
			+ "2: n5@0/4|_@0/4 n6@1/4|_@1/4 n7@2/4|_@2/4 n8@3/4|_@3/4\n"
			+ "returned: 1@0.0 1@2.0"
			, replaceAt(0, 0));
	}
}
