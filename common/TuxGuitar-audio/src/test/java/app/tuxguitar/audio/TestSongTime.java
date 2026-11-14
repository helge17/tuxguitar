package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;

/**
 * Song time and its sample clock: which measures play from a position, following repeats, and where a tick of a
 * played measure sounds. The song has 4 measures of 4/4 at 120 bpm (88200 samples each at 44100 Hz).
 */
public class TestSongTime {

	private static final int QUARTER = 960;
	private static final int BAR = 4 * QUARTER;

	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();
	private final long origin;

	public TestSongTime() {
		for (int m = 1; m < 4; m++) {
			songManager.addNewMeasureBeforeEnd(song);
		}
		origin = song.getMeasureHeader(0).getStart();
	}

	// "m<number> <from>-<to>" for each measure played, ticks from its start
	private String played(long start, long stop) {
		List<String> measures = new ArrayList<String>();
		for (TGPlayedMeasure measure : TGSongTime.play(song, new MidiPlayerMode(), origin + start, origin + stop, 1)) {
			measures.add("m" + measure.getHeader().getNumber() + " " + measure.getFrom() + "-" + measure.getTo());
		}
		return String.join(", ", measures);
	}

	// measures 1 and 2 repeated once: playing from measure 3 starts after the repeat, where measure 2 ends
	@Test
	public void playingFromWhereARepeatEndsPlaysOnFromThere() {
		song.getMeasureHeader(0).setRepeatOpen(true);
		song.getMeasureHeader(1).setRepeatClose(1);

		assertEquals("m3 0-3840, m4 0-3840", played(2 * BAR, 4 * BAR));
	}

	// played from beat 2, measure 1's beat 3 sounds one beat (22050 samples) after playing starts
	@Test
	public void aTickOfTheFirstMeasureSoundsCountedFromWherePlayingStarts() {
		TGTimeline timeline = new TGTimeline(song, TGSongTime.play(song, new MidiPlayerMode(), origin + QUARTER, origin + BAR, 1), 44100);

		assertEquals(22050, timeline.sampleAt(0, 2 * QUARTER));
	}
}
