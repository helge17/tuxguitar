package app.tuxguitar.song.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins TGBeatRangeNoteIterator: the notes of the chosen voices of every beat from the start beat to the end beat,
 * across measures, beat by beat, and within a beat voice by voice in the order the voices are given. Beats and
 * voices without notes are skipped. Notes are read back by fret.
 *
 * The song has three 4/4 measures:
 * measure 1, beat 0: voice 0 frets 1 and 2, voice 1 fret 3; beat 1: voice 1 fret 4;
 * measure 2, beat 0: no notes; measure 3, beat 0: voice 0 fret 5.
 */
public class TestBeatRangeNoteIterator {

	private final TGSongManager songManager = new TGSongManager();
	private final TGFactory factory = songManager.getFactory();
	private final TGSong song = songManager.newSong();
	private final TGTrack track = song.getTrack(0);

	private final TGBeat first;
	private final TGBeat second;
	private final TGBeat silent;
	private final TGBeat last;

	public TestBeatRangeNoteIterator() {
		songManager.addNewMeasureBeforeEnd(song);
		songManager.addNewMeasureBeforeEnd(song);
		first = beat(1, 0);
		second = beat(1, 1);
		silent = beat(2, 0);
		last = beat(3, 0);
		notes(first, 0, 1, 2);
		notes(first, 1, 3);
		notes(second, 1, 4);
		notes(last, 0, 5);
	}

	private TGBeat beat(int measureNumber, int quarter) {
		TGMeasure measure = track.getMeasure(measureNumber - 1);
		TGBeat beat = factory.newBeat();
		beat.setPreciseStart(measure.getPreciseStart() + quarter * (TGDuration.WHOLE_PRECISE_DURATION / 4));
		songManager.getMeasureManager().addBeat(measure, beat);
		return beat;
	}

	private void notes(TGBeat beat, int voice, int... frets) {
		for (int fret : frets) {
			TGNote note = factory.newNote();
			note.setValue(fret);
			beat.getVoice(voice).addNote(note);
		}
	}

	private static List<Integer> frets(TGBeat start, TGBeat end, Collection<Integer> voices) {
		return new TGBeatRangeNoteIterator(start, end, voices).toList().stream().map(TGNote::getValue).collect(Collectors.toList());
	}

	private static DynamicTest reads(String name, TGBeat start, TGBeat end, List<Integer> voices, List<Integer> expected) {
		return DynamicTest.dynamicTest(name, () -> assertEquals(expected, frets(start, end, voices)));
	}

	@TestFactory
	public Stream<DynamicTest> readsTheNotesOfTheChosenVoicesOfEveryBeatInTheRange() {
		return Stream.of(
			reads("every beat", first, last, List.of(0, 1), List.of(1, 2, 3, 4, 5)),
			reads("voices in the given order", first, last, List.of(1, 0), List.of(3, 1, 2, 4, 5)),
			reads("only voice 1", first, last, List.of(1), List.of(3, 4)),
			reads("only voice 0", first, last, List.of(0), List.of(1, 2, 5)),
			reads("from the second beat", second, last, List.of(0, 1), List.of(4, 5)),
			reads("up to the second beat", first, second, List.of(0, 1), List.of(1, 2, 3, 4)));
	}

	@Test
	public void nextSkipsTheBeatsWithoutNotesByItself() {
		assertEquals(5, new TGBeatRangeNoteIterator(second, last, List.of(0)).next().getValue());
	}

	@Test
	public void aRangeWithoutNotesHasNoNext() {
		TGBeatRangeNoteIterator notes = new TGBeatRangeNoteIterator(silent, silent, List.of(0, 1));

		assertFalse(notes.hasNext());
		assertThrows(NoSuchElementException.class, notes::next);
		assertThrows(UnsupportedOperationException.class, notes::remove);
	}
}
