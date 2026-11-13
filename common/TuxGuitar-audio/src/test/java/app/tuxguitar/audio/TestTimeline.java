package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerCountDown;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

/**
 * What plays on a timeline: the Count In before its first measure, and where it stops when stopped at a sample. The
 * song has 4 measures of 4/4 at 120 bpm: 88200 samples each at 44100 Hz, a beat 22050, a tick about 23. Read as
 * "<clock> | <Count In click positions> | <measure>:<from>-<to> ... | <length>".
 */
public class TestTimeline {

	private static final int QUARTER = 960;
	private static final int BAR = 4 * QUARTER;

	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();
	private final MidiPlayerCountDown countDown = MidiPlayer.getInstance(new TGContext()).getCountDown();
	private final long origin;

	public TestTimeline() {
		for (int m = 1; m < 4; m++) {
			songManager.addNewMeasureBeforeEnd(song);
		}
		origin = song.getMeasureHeader(0).getStart();
	}

	// played from measure 2 to the end
	private TGTimeline fromMeasure2() {
		List<TGPlayedMeasure> played = TGSongTime.play(song, new MidiPlayerMode(), origin + BAR, Long.MAX_VALUE, 1);
		return new TGTimeline(song, played, 44100, TGCountIn.before(countDown, played, 44100));
	}

	private static String read(TGTimeline timeline) {
		StringBuilder read = new StringBuilder(timeline.getClock() + " | " + Arrays.toString(timeline.getCountIn().positions()) + " |");
		for (int i = 0; i < timeline.count(); i++) {
			TGPlayedMeasure measure = timeline.get(i);
			read.append(" m" + measure.getHeader().getNumber() + ":" + measure.getFrom() + "-" + measure.getTo());
		}
		return read.append(" | " + timeline.length()).toString();
	}

	@Test
	public void theCountInComesBeforeTheFirstMeasureAndMovesTheClockBack() {
		countDown.setEnabled(true);
		TGTimeline timeline = fromMeasure2();

		assertEquals("0 | [0, 22050, 44100, 66150] | m2:0-3840 m3:0-3840 m4:0-3840 | 352800", read(timeline));
		assertEquals(88200, timeline.start(0));
	}

	@Test
	public void withCountInOffTheFirstMeasureStartsPlaying() {
		assertEquals("88200 | [] | m2:0-3840 m3:0-3840 m4:0-3840 | 264600", read(fromMeasure2()));
	}

	// stopped where a measure starts; 1000 samples into measure 3 (up to its first tick sounding there or later: tick 44,
	// at sample 1011); on the sample of its beat 2 (tick 960, not after it); in the Count In, on the third click (which
	// does not sound); after it all
	@Test
	public void stoppedAtASampleItPlaysWhatComesBeforeIt() {
		String atAMeasure = read(fromMeasure2().until(88200));
		String insideAMeasure = read(fromMeasure2().until(89200));
		String onATick = read(fromMeasure2().until(110250));
		countDown.setEnabled(true);
		String inTheCountIn = read(fromMeasure2().until(44100));
		String afterItAll = read(fromMeasure2().until(Long.MAX_VALUE));

		assertEquals(List.of("88200 | [] | m2:0-3840 | 88200", "88200 | [] | m2:0-3840 m3:0-44 | 89211", "88200 | [] | m2:0-3840 m3:0-960 | 110250",
			"0 | [0, 22050] | | 44100", read(fromMeasure2())), List.of(atAMeasure, insideAMeasure, onATick, inTheCountIn, afterItAll));
	}
}
