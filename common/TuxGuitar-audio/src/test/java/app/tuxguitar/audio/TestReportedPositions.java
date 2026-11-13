package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerCountDown;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

/**
 * Where the player reports the song is after each block of 512 samples of a timeline: the song tick the output sounds
 * then, a delay behind the clock. The song has 4 measures of 4/4 at 120 bpm: measure 2 starts at tick 4800, a tick lasts
 * 22.96875 samples at 44100 Hz, a measure 88200 samples.
 */
public class TestReportedPositions {

	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();
	private final MidiPlayerCountDown countDown = MidiPlayer.getInstance(new TGContext()).getCountDown();
	private final MidiPlayerMode mode = new MidiPlayerMode();

	public TestReportedPositions() {
		for (int m = 1; m < 4; m++) {
			songManager.addNewMeasureBeforeEnd(song);
		}
	}

	private long start(int measure) {
		return song.getMeasureHeader(measure - 1).getStart();
	}

	// played from the tick, for the passes, at the sample rate
	private TGTimeline from(long tick, int passes, int sampleRate) {
		List<TGPlayedMeasure> played = TGSongTime.play(song, mode, tick, Long.MAX_VALUE, passes);
		return new TGTimeline(song, played, sampleRate, TGCountIn.before(countDown, played, sampleRate));
	}

	private TGTimeline fromMeasure2(int passes) {
		return from(start(2), passes, 44100);
	}

	// the positions reported after the blocks, from the first
	private static String reported(TGTimeline timeline, int delay, int first, int count) {
		return Arrays.stream(TGReportedPositions.of(timeline, delay), first, first + count).mapToObj(String::valueOf).collect(Collectors.joining(" "));
	}

	// without a delay and 1024 samples late: until the output sounds where playing starts the position is before it
	@Test
	public void afterEachBlockThePositionIsTheTickTheOutputSounds() {
		assertEquals("4822 4844 | 4777 4800 4822", reported(fromMeasure2(1), 0, 0, 2) + " | " + reported(fromMeasure2(1), 1024, 0, 3));
	}

	// playing from beat 2 of measure 2 (tick 5760)
	@Test
	public void playingFromInsideAMeasureStartsAtItsTick() {
		assertEquals("5782", reported(from(start(2) + 960, 1, 44100), 0, 0, 1));
	}

	// in the Count In (4 beats, 88200 samples) the position is before the first measure played: after the first block,
	// tick 982 before measure 2, tick 8662 before measure 4 (played alone)
	@Test
	public void inTheCountInThePositionIsBeforeTheFirstMeasure() {
		countDown.setEnabled(true);

		assertEquals("982 | 8662", reported(fromMeasure2(1), 0, 0, 1) + " | " + reported(from(start(4), 1, 44100), 0, 0, 1));
	}

	// measure 3 at 60 bpm: measure 2 is reported at its own tempo; measure 3 from its start (sample 88200, in block 172) at
	// 60 bpm, a tick every 45.9375 samples
	@Test
	public void eachMeasureIsReportedAtItsTempo() {
		song.getMeasureHeader(2).getTempo().setQuarterValue(60);

		assertEquals("4822 | 8648", reported(fromMeasure2(1), 0, 0, 1) + " | " + reported(fromMeasure2(1), 0, 172, 1));
	}

	private void loopMeasures2To3() {
		mode.setLoop(true);
		mode.setLoopSHeader(2);
		mode.setLoopEHeader(3);
	}

	// the loop jumps back to measure 2 at sample 176400, in block 344: the output sounds the jump 1024 samples later, at
	// the end of block 346
	@Test
	public void aJumpBackIsReportedWhenTheOutputSoundsIt() {
		loopMeasures2To3();

		assertEquals("12445 12468 4810", reported(fromMeasure2(2), 1024, 344, 3));
	}

	// at 32768 Hz a measure is 65536 samples: the jump back is on the first frame of block 256, so after block 255 the end
	// of measure 3 is reported (as live, where the next block plays the jump), after block 256 the jump
	@Test
	public void aJumpOnTheFirstFrameOfABlockIsReportedAfterThatBlock() {
		loopMeasures2To3();

		assertEquals("12480 4830", reported(from(start(2), 2, 32768), 0, 255, 2));
	}

	// the last block runs past the end of the timeline (3 measures: 516.8 blocks), or ends with it (104 samples more)
	@Test
	public void theBlocksCoverTheWholeTimeline() {
		assertEquals("517 517", TGReportedPositions.of(fromMeasure2(1), 0).length + " " + TGReportedPositions.of(fromMeasure2(1).runningOn(104), 0).length);
	}

	// with nothing played (stopped in the Count In) the position is 0
	@Test
	public void withNothingPlayedThePositionIsZero() {
		countDown.setEnabled(true);

		assertEquals("0", reported(fromMeasure2(1).until(30000), 0, 0, 1));
	}
}
