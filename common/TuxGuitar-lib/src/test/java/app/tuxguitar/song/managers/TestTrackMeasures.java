package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.helpers.SegmentFixture;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins how TGTrackManager finds the measures of a track by time, and changes the key signature or clef of a measure,
 * or of every measure from it to the end. The song is the {@link SegmentFixture}; measures are named by number.
 */
public class TestTrackMeasures extends SegmentFixture {

	private final TGTrackManager manager = songManager.getTrackManager();
	private final TGTrack track = song.getTrack(0);

	private static String numbers(List<TGMeasure> measures) {
		List<String> numbers = new ArrayList<String>();
		for (TGMeasure measure : measures) {
			numbers.add(String.valueOf(measure.getNumber()));
		}
		return String.join(" ", numbers);
	}

	// the numbers of the measures of track 1 whose note is spelled with its alternative enharmonic
	private String altSpelled() {
		List<TGMeasure> measures = new ArrayList<TGMeasure>();
		for (int m = 0; m < track.countMeasures(); m++) {
			if (track.getMeasure(m).getBeat(0).getVoice(0).getNote(0).isAltEnharmonic()) {
				measures.add(track.getMeasure(m));
			}
		}
		return numbers(measures);
	}

	private void spellAllAlt() {
		for (int m = 0; m < track.countMeasures(); m++) {
			track.getMeasure(m).getBeat(0).getVoice(0).getNote(0).toggleAltEnharmonic();
		}
	}

	@Test
	public void theMeasuresBetweenTwoTimesAreThoseSoundingStrictlyInsideThem() {
		assertEquals("2", numbers(manager.getMeasuresBetween(track, start(2), start(3))));
		assertEquals("1 2 3", numbers(manager.getMeasuresBetween(track, start(2) - 1, start(3) + 1)));
		assertEquals("3", numbers(manager.getMeasuresFrom(track, start(3))));
		assertEquals("", numbers(manager.getMeasuresBetween(track, start(3), start(3))));
	}

	@Test
	public void theFirstMeasureIsTheOneStartingFirstWhereverItIsInTheTrack() {
		assertEquals(1, manager.getFirstMeasure(track).getNumber());

		song.getMeasureHeader(0).setStart(start(3) + 1);
		assertEquals(2, manager.getFirstMeasure(track).getNumber());
	}

	@Test
	public void theKeySignatureChangesInTheMeasureOnlyOrToTheEndAndResetsTheirSpelling() {
		spellAllAlt();
		manager.changeKeySignature(track, track.getMeasure(1), 5, false);
		assertEquals(""
			+ "track 1: 1@1/1/1:11+0 2@5/5/2:12+0 3@9/3/3:13+0\n"
			+ "track 2: 1@1/1/1:21+0 2@5/2/2:22+0 3@9/3/3:23+0\n"
			, describe());
		assertEquals("1 3", altSpelled());

		manager.changeKeySignature(track, track.getMeasure(1), 6, true);
		assertEquals(""
			+ "track 1: 1@1/1/1:11+0 2@5/6/2:12+0 3@9/6/3:13+0\n"
			+ "track 2: 1@1/1/1:21+0 2@5/2/2:22+0 3@9/3/3:23+0\n"
			, describe());
		assertEquals("1", altSpelled());
	}

	@Test
	public void theClefChangesInTheMeasureOnlyOrToTheEnd() {
		manager.changeClef(track, track.getMeasure(1), 4, false);
		assertEquals(""
			+ "track 1: 1@1/1/1:11+0 2@5/2/4:12+0 3@9/3/3:13+0\n"
			+ "track 2: 1@1/1/1:21+0 2@5/2/2:22+0 3@9/3/3:23+0\n"
			, describe());

		manager.changeClef(track, track.getMeasure(1), 1, true);
		assertEquals(""
			+ "track 1: 1@1/1/1:11+0 2@5/2/1:12+0 3@9/3/1:13+0\n"
			+ "track 2: 1@1/1/1:21+0 2@5/2/2:22+0 3@9/3/3:23+0\n"
			, describe());
	}
}
