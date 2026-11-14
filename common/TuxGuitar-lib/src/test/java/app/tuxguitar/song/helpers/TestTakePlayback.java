package app.tuxguitar.song.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.models.TGTakeClip;
import app.tuxguitar.song.models.TGTakeSegment;

/**
 * Pins TGTakePlayback inside one measure: a clip plays from its start up to, not including, its end, so where a
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
		for (TGTakeSegment segment : TGTakePlayback.of(measure)) {
			segments.add(segment.getStart() + ":" + (segment.getTake() == null ? "none" : segment.getTake().getNumber()));
		}
		return String.join(" ", segments);
	}

	@Test
	public void theOlderTakePlaysAgainWhereTheNewerEnds() {
		measure.addTakeClip(new TGTakeClip(new TGTake(1, new float[0], 44100), 0, 3840, 0, 120));
		measure.addTakeClip(new TGTakeClip(new TGTake(2, new float[0], 44100), 960, 1920, 0, 120));

		assertEquals("0:1 960:2 1920:1", playback());
	}

	// "take:start-end@first sample" of each clip that plays
	private String clips() {
		List<String> clips = new ArrayList<String>();
		for (TGTakeClip clip : TGTakePlayback.clips(measure)) {
			clips.add(clip.getTake().getNumber() + ":" + clip.getStart() + "-" + clip.getEnd() + "@" + clip.getFirstSample());
		}
		return String.join(" ", clips);
	}

	@Test
	public void theClipsThatPlayAreCutWhereAnotherTakeTakesOver() {
		measure.addTakeClip(new TGTakeClip(new TGTake(1, new float[0], 44100), 0, 3840, 0, 120));
		measure.addTakeClip(new TGTakeClip(new TGTake(2, new float[0], 44100), 960, 1920, 500, 120));

		assertEquals("1:0-960@0 2:960-1920@500 1:1920-3840@44100", clips());
	}

	@Test
	public void aMeasureWithoutTakesPlaysNoClip() {
		assertEquals("", clips());
	}
}
