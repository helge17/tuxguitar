package app.tuxguitar.song.managers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.io.tg.TestFileFormat20;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;

public class TestAutoCompleteSilences {

	static final TGMeasureManager mgr = new TGSongManager().getMeasureManager();

	/* Description of test:
	 * - import test file, build list of all rest beats
	 * - delete all rest beats
	 * - call autoCompleteSilences, to recreate the rests
	 * - compare the result, measure by measure, with original test file
	 * 
	 * The test file organizes the rest as expected
	 * 
	 */
	@Test
	public void testAutoComplete() throws IOException {
		TGSong song = new TestFileFormat20().readSong("alignRestsTest_22.tg", true).getSong();

		Iterator<TGMeasure> measures = song.getTrack(0).getMeasures();
		while (measures.hasNext()) {
			testMeasure(measures.next());
		}
	}

	private void testMeasure(TGMeasure measure) {
		List<Rest> originalRests = new ArrayList<Rest>();
		int nMeas = measure.getNumber();

		// build original list of rests, and remove them
		List<TGBeat> toRemove = new ArrayList<TGBeat>();
		for (TGBeat beat : measure.getBeats()) {
			if (beat.isRestBeat()) {
				originalRests.add(new Rest(beat.getPreciseStart(), beat.getVoice(0).getDuration()));
				toRemove.add(beat);
			}
		}
		for (TGBeat beat : toRemove) {
			mgr.removeBeat(beat);
		}
		// re-create the rests
		mgr.autoCompleteSilences(measure);

		// compare
		int nRest = 0;
		for (TGBeat beat : measure.getBeats()) {
			if (beat.isRestBeat()) {
				assertTrue(nRest <= originalRests.size()-1, "meas " + String.valueOf(nMeas) + ", too many rests: " + String.valueOf(nRest+1));
				Rest expected = originalRests.get(nRest);
				assertEquals(expected.preciseStart, beat.getPreciseStart(),
						"meas " + String.valueOf(nMeas) + "rest #" + String.valueOf(nRest) + ", incorrect start");
				TGDuration d = beat.getVoice(0).getDuration();
				assertTrue(expected.duration.isEqual(d),
						"meas " + String.valueOf(nMeas) + "rest #" + String.valueOf(nRest) + ", incorrect duration: " + durationToString(d) + " / expected: " + durationToString(expected.duration));
				nRest++;
			}
		}
	}

	private String durationToString(TGDuration d) {
		return String.valueOf(d.getValue()) + " (" 
				+ String.valueOf(d.getDivision().getEnters()) + ":"
				+ String.valueOf(d.getDivision().getTimes()) +")";
		
	}
}

class Rest {
	long preciseStart;
	TGDuration duration;
	
	Rest(long preciseStart, TGDuration duration) {
		this.preciseStart = preciseStart;
		this.duration = TestAutoCompleteSilences.mgr.getSongManager().getFactory().newDuration();
		this.duration.copyFrom(duration);
	}
}

