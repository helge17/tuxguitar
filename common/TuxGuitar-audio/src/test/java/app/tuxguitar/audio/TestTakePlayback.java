package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.models.TGTakeClip;

/**
 * Pins TGTakes.playback inside one measure: a clip plays from its start up to, not including, its end, so where a
 * newer take ends inside the measure the older one plays again from that tick.
 *
 * The measure is 4/4 (3840 ticks); it reads back as "tick:take" per segment.
 */
public class TestTakePlayback {

	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();
	private final TGMeasure measure = song.getTrack(0).getMeasure(0);

	private String playback() {
		List<String> segments = new ArrayList<String>();
		for (TGTakeSegment segment : TGTakes.playback(measure)) {
			segments.add(segment.getStart() + ":" + (segment.getTake() == null ? "none" : segment.getTake().getNumber()));
		}
		return String.join(" ", segments);
	}

	@Test
	public void theOlderTakePlaysAgainWhereTheNewerEnds() {
		measure.addTakeClip(new TGTakeClip(new TGTake(1, new float[0]), 0, 3840, 0, 120));
		measure.addTakeClip(new TGTakeClip(new TGTake(2, new float[0]), 960, 1920, 0, 120));

		assertEquals("0:1 960:2 1920:1", playback());
	}
}
