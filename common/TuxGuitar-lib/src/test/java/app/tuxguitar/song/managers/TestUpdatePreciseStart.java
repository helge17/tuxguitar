package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;

/**
 * Re-deriving precise starts: a song of three 4/4 measures, each holding one quarter rest at its start.
 */
public class TestUpdatePreciseStart {

	private final TGSongManager manager = new TGSongManager();
	private final TGSong song = threeMeasureSong();

	private TGSong threeMeasureSong() {
		TGSong song = manager.newSong();
		manager.addNewMeasureBeforeEnd(song);
		manager.addNewMeasureBeforeEnd(song);
		for (int index = 0; index < 3; index++) {
			TGMeasure measure = song.getTrack(0).getMeasure(index);
			TGBeat beat = manager.getFactory().newBeat();
			beat.setPreciseStart(measure.getPreciseStart());
			beat.getVoice(0).setEmpty(false);
			manager.getMeasureManager().addBeat(measure, beat);
		}
		return song;
	}

	private TGMeasure measure(int index) {
		return song.getTrack(0).getMeasure(index);
	}

	private TGBeat beat(int index) {
		return measure(index).getBeat(0);
	}

	@Test
	public void beatsAreRealignedToTheirMeasureStart() {
		long expected = measure(2).getPreciseStart();
		beat(2).setPreciseStart(expected + 7);

		manager.updatePreciseStart(song);

		assertEquals(expected, beat(2).getPreciseStart());
	}

	@Test
	public void measuresAfterThePointAreLaidOutAgainAndTheirBeatsRealigned() {
		long first = measure(0).getPreciseStart();
		long second = measure(1).getPreciseStart();
		long third = measure(2).getPreciseStart();
		measure(1).getHeader().setPreciseStart(second + 5);
		measure(2).getHeader().setPreciseStart(third + 5);
		beat(2).setPreciseStart(third + 7);

		manager.updatePreciseStart(song, first);

		assertEquals(first, measure(0).getPreciseStart());
		assertEquals(second, measure(1).getPreciseStart());
		assertEquals(third, measure(2).getPreciseStart());
		assertEquals(third, beat(2).getPreciseStart());
	}

	@Test
	public void measuresUpToAndAtThePointAreLeftInPlace() {
		long second = measure(1).getPreciseStart();
		measure(1).getHeader().setPreciseStart(second - 5);

		manager.updatePreciseStart(song, second - 5);

		assertEquals(second - 5, measure(1).getPreciseStart());
		assertEquals(measure(1).getPreciseStart(), beat(1).getPreciseStart());
	}
}
