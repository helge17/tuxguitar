package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGText;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins how beats lying beyond their 4/4 measure are moved where they belong
 * (TGMeasureManager.moveOutOfBoundsBeatsToNewMeasure, through locateBeat): into the measure holding their start,
 * with a voice running past its measure end cut there and continued by tied notes in the following measures,
 * and measures added at the end of the song when needed. Also pins TGTrackManager.moveTrackBeatsPrecise, which
 * shifts the beats of a track from a point on, then moves those left beyond their measure this way.
 *
 * Tracks are described as in TrackDescription.
 */
public class TestMoveOutOfBoundsBeats {

	private static final long QUARTER = TrackDescription.QUARTER;

	private final TGSongManager songManager = new TGSongManager();
	private final TGFactory factory = songManager.getFactory();
	private final TGMeasureManager manager = songManager.getMeasureManager();
	private final TGSong song = songManager.newSong();
	private final TGTrack track = song.getTrack(0);

	private TestMoveOutOfBoundsBeats measures(int count) {
		while (song.countMeasureHeaders() < count) {
			songManager.addNewMeasureBeforeEnd(song);
		}
		return this;
	}

	// a beat put straight into the measure, without any check, "offset" from the measure start
	private TGVoice beat(int measureIndex, long offset, int value, boolean dotted) {
		TGMeasure measure = track.getMeasure(measureIndex);
		TGBeat beat = factory.newBeat();
		beat.setPreciseStart(measure.getPreciseStart() + offset);
		TGVoice voice = beat.getVoice(0);
		voice.setEmpty(false);
		voice.getDuration().setValue(value);
		voice.getDuration().setDotted(dotted);
		measure.addBeat(beat);
		return voice;
	}

	private TGVoice note(int measureIndex, long offset, int value, boolean dotted, int fret) {
		TGVoice voice = beat(measureIndex, offset, value, dotted);
		TGNote note = factory.newNote();
		note.setString(1);
		note.setValue(fret);
		note.setVelocity(95);
		voice.addNote(note);
		return voice;
	}

	private String describe() {
		return TrackDescription.describe(track);
	}

	@Test
	public void aBeatPastTheMeasureEndMovesToTheNextMeasure() {
		measures(2).note(0, 5 * QUARTER, TGDuration.QUARTER, false, 3);
		manager.moveOutOfBoundsBeatsToNewMeasure(track.getMeasure(0));
		assertEquals(""
			+ "1:\n"
			+ "2: n3@1/4\n"
			, describe());
	}

	@Test
	public void aBeatBeforeTheMeasureStartMovesToThePreviousMeasure() {
		measures(2).note(1, -QUARTER, TGDuration.QUARTER, false, 3);
		manager.moveOutOfBoundsBeatsToNewMeasure(track.getMeasure(1));
		assertEquals(""
			+ "1: n3@3/4\n"
			+ "2:\n"
			, describe());
	}

	@Test
	public void aNoteRunningPastTheMeasureEndIsTiedIntoTheNextMeasure() {
		measures(2).note(0, 3 * QUARTER, TGDuration.HALF, false, 3);
		manager.moveOutOfBoundsBeatsToNewMeasure(track.getMeasure(0));
		assertEquals(""
			+ "1: n3@3/4\n"
			+ "2: n3~@0/4\n"
			, describe());
	}

	@Test
	public void aLongNoteIsTiedOverSeveralMeasures() {
		measures(3).note(0, 3 * QUARTER, TGDuration.WHOLE, true, 3);
		manager.moveOutOfBoundsBeatsToNewMeasure(track.getMeasure(0));
		assertEquals(""
			+ "1: n3@3/4\n"
			+ "2: n3~@0/1\n"
			+ "3: n3~@0/4\n"
			, describe());
	}

	@Test
	public void theTiedNoteJoinsABeatAlreadyThere() {
		measures(2).note(0, 3 * QUARTER, TGDuration.HALF, false, 3);
		note(1, 0, TGDuration.QUARTER, false, 7).getNote(0).setString(2);
		manager.moveOutOfBoundsBeatsToNewMeasure(track.getMeasure(0));
		assertEquals(""
			+ "1: n3@3/4\n"
			+ "2: n7,3~@0/4\n"
			, describe());
	}

	@Test
	public void aChordIsTiedNoteByNote() {
		TGVoice voice = measures(2).note(0, 3 * QUARTER, TGDuration.HALF, false, 3);
		TGNote second = factory.newNote();
		second.setString(2);
		second.setValue(5);
		second.setVelocity(60);
		voice.addNote(second);
		manager.moveOutOfBoundsBeatsToNewMeasure(track.getMeasure(0));
		assertEquals(""
			+ "1: n3,5v60@3/4\n"
			+ "2: n3~,5~v60@0/4\n"
			, describe());
	}

	@Test
	public void aNoteRunningPastTheLastMeasureAddsMeasures() {
		measures(1).note(0, 3 * QUARTER, TGDuration.WHOLE, true, 3);
		manager.moveOutOfBoundsBeatsToNewMeasure(track.getMeasure(0));
		assertEquals(""
			+ "1: n3@3/4\n"
			+ "2: n3~@0/1\n"
			+ "3: n3~@0/4\n"
			, describe());
	}

	@Test
	public void aNotePastTheEndOfTheSongAddsMeasures() {
		measures(1).note(0, 9 * QUARTER, TGDuration.QUARTER, false, 3);
		manager.moveOutOfBoundsBeatsToNewMeasure(track.getMeasure(0), false);
		assertEquals(""
			+ "1:\n"
			+ "2:\n"
			+ "3: n3@1/4\n"
			, describe());
	}

	@Test
	public void aRestPastTheEndOfTheSongIsDroppedUnlessAsked() {
		measures(1).beat(0, 5 * QUARTER, TGDuration.QUARTER, false);
		manager.moveOutOfBoundsBeatsToNewMeasure(track.getMeasure(0), false);
		assertEquals(""
			+ "1:\n"
			, describe());

		beat(0, 5 * QUARTER, TGDuration.QUARTER, false);
		manager.moveOutOfBoundsBeatsToNewMeasure(track.getMeasure(0), true);
		assertEquals(""
			+ "1:\n"
			+ "2: r@1/4\n"
			, describe());
	}

	@Test
	public void aRestWithATextPastTheEndOfTheSongAddsMeasures() {
		TGText text = factory.newText();
		text.setValue("text");
		measures(1).beat(0, 5 * QUARTER, TGDuration.QUARTER, false).getBeat().setText(text);
		manager.moveOutOfBoundsBeatsToNewMeasure(track.getMeasure(0), false);
		assertEquals(""
			+ "1:\n"
			+ "2: t@1/4\n"
			, describe());
	}

	@Test
	public void anEmptyVoiceIsNotSplit() {
		TGVoice voice = measures(2).note(0, 3 * QUARTER, TGDuration.QUARTER, false, 3);
		voice.getBeat().getVoice(1).setEmpty(false);
		voice.getBeat().getVoice(1).getDuration().setValue(TGDuration.WHOLE);
		voice.getBeat().getVoice(1).setEmpty(true);
		voice.getBeat().setPreciseStart(voice.getBeat().getPreciseStart() + 2 * QUARTER);
		manager.moveOutOfBoundsBeatsToNewMeasure(track.getMeasure(0));
		assertEquals(""
			+ "1:\n"
			+ "2: n3@1/4\n"
			, describe());
	}

	// quarter notes of frets 1 to 4 filling measure 1, and of fret 5 starting measure 2
	private TestMoveOutOfBoundsBeats fiveNotes() {
		measures(2);
		for (int q = 0; q < 4; q++) {
			note(0, q * QUARTER, TGDuration.QUARTER, false, q + 1);
		}
		note(1, 0, TGDuration.QUARTER, false, 5);
		return this;
	}

	// TGTrackManager.moveTrackBeatsPrecise, from the second quarter of measure 1, as moving beats does
	private void moveTrackBeats(long thePreciseMove) {
		TGMeasure first = track.getMeasure(0);
		songManager.getTrackManager().moveTrackBeatsPrecise(track, first.getStart(), first.getPreciseStart() + QUARTER, thePreciseMove);
	}

	@Test
	public void movingTheTrackBeatsLaterPushesTheLastBeatOfAMeasureIntoTheNext() {
		fiveNotes().moveTrackBeats(QUARTER);
		assertEquals(""
			+ "1: n1@0/4 n2@2/4 n3@3/4\n"
			+ "2: n4@0/4 n5@1/4\n"
			, describe());
	}

	@Test
	public void movingTheTrackBeatsEarlierRemovesTheBeatsCoveredAndPullsTheNextMeasureBack() {
		fiveNotes().moveTrackBeats(-QUARTER);
		assertEquals(""
			+ "1: n1@0/4 n3@1/4 n4@2/4 n5@3/4\n"
			+ "2:\n"
			, describe());
	}

	@Test
	public void movingTheTrackBeatsLeavesTheMeasuresBeforeTheStartMeasureAlone() {
		fiveNotes();
		TGMeasure second = track.getMeasure(1);
		songManager.getTrackManager().moveTrackBeatsPrecise(track, second.getStart(), track.getMeasure(0).getPreciseStart(), QUARTER);
		assertEquals(""
			+ "1: n1@0/4 n2@1/4 n3@2/4 n4@3/4\n"
			+ "2: n5@1/4\n"
			, describe());
	}

	@Test
	public void aNoteThatCannotBeCutIsLeftWhole() {
		measures(2).note(0, 3 * QUARTER + 1, TGDuration.HALF, false, 3);
		manager.moveOutOfBoundsBeatsToNewMeasure(track.getMeasure(0));
		assertEquals(""
			+ "1: n3@3.0000003468753467/2\n"
			+ "2:\n"
			, describe());
	}
}
