package app.tuxguitar.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins TGBeatRange.containsMeasure: a measure is in the range when it is the very measure of one of its beats; a
 * measure of another track at the same position, or a measure without beats in the range, is not.
 *
 * The song has two tracks of three measures; the range holds the first beats of measures 1 and 3 of track 1.
 */
public class TestBeatRange {

	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();
	private final TGTrack first;
	private final TGTrack second;

	public TestBeatRange() {
		songManager.addNewMeasureBeforeEnd(song);
		songManager.addNewMeasureBeforeEnd(song);
		first = song.getTrack(0);
		second = songManager.addTrack(song);
	}

	// a new beat at the start of the measure
	private TGBeat firstBeat(TGTrack track, int measureIndex) {
		TGMeasure measure = track.getMeasure(measureIndex);
		TGBeat beat = songManager.getFactory().newBeat();
		beat.setStart(measure.getStart());
		measure.addBeat(beat);
		return beat;
	}

	// which measures of a track the range contains, as "number:yes|no" for every measure
	private String contained(TGBeatRange range, TGTrack track) {
		StringBuilder contained = new StringBuilder();
		for (int i = 0; i < track.countMeasures(); i++) {
			TGMeasure measure = track.getMeasure(i);
			contained.append(measure.getNumber()).append(range.containsMeasure(measure) ? ":yes " : ":no ");
		}
		return contained.toString().trim();
	}

	@Test
	public void containsTheMeasuresOfItsBeatsOnly() {
		TGBeatRange range = new TGBeatRange(List.of(firstBeat(first, 0), firstBeat(first, 2)));

		assertEquals("1:yes 2:no 3:yes", contained(range, first));
		assertEquals("1:no 2:no 3:no", contained(range, second));
	}

	@Test
	public void anEmptyRangeContainsNoMeasure() {
		assertEquals("1:no 2:no 3:no", contained(TGBeatRange.empty(), first));
	}

	@Test
	public void aSingleBeatRangeContainsItsMeasure() {
		assertEquals("1:no 2:yes 3:no", contained(TGBeatRange.single(firstBeat(first, 1)), first));
	}
}
