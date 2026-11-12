package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Pins how the player starts to play (MidiPlayer.play): it moves the sequencer to its position and hands the start to
 * a thread, which waits for the synthesizer, notifies the start when the player was stopped, starts the sequencer and
 * hands the play loop on. A stop returns to where the play began; with a complete selection, to where its first play
 * began. A failure stops the player and is reported.
 *
 * The song has 4 measures and plays through a FakeSequencer (see PlayingFixture), its threads run step by step.
 */
public class TestMidiPlayerPlay {

	private final PlayingFixture fixture = new PlayingFixture();
	private final MidiPlayer player = fixture.player;
	private final SteppedThreads threads = fixture.threads;

	// plays from the position, then runs the start: what playing logged | what starting logged | what the start's loop
	// asked | what is left to run
	private String playFrom(long position) throws MidiPlayerException {
		player.setTickPosition(position);
		player.play();
		String playing = fixture.takeLog();
		threads.runStarted();
		Long asked = threads.runLoop();
		return playing + " | " + fixture.takeLog() + " | " + asked + " | " + threads.pending();
	}

	// plays from one position, then from another: where a stop then returns to
	private long stopAfterPlayingFrom(long first, long second) throws MidiPlayerException {
		player.setTickPosition(first);
		player.play();
		player.setTickPosition(second);
		player.play();
		player.reset();
		return player.getTickPosition();
	}

	@Test
	public void playingMovesTheSequencerThenStartsItNotifyingOnlyAStoppedPlayer() throws MidiPlayerException {
		String stopped = playFrom(5000);
		String running = playFrom(6000);

		assertEquals("seek 5000 | started, start | -1 | 0 started, 1 looped"
				+ " / seek 6000 | start | -1 | 0 started, 2 looped", stopped + " / " + running);
	}

	@Test
	public void playingBoundsTheLoopAnewSoItBeginsNoEarlierThanTheLoop() throws MidiPlayerException {
		fixture.loopMode(2, 3);
		player.play();

		assertEquals("seek " + fixture.start(2), fixture.takeLog());
	}

	@Test
	public void withoutASelectionAStopReturnsToWhereTheLastPlayBegan() throws MidiPlayerException {
		assertEquals(6000, stopAfterPlayingFrom(5000, 6000));
	}

	@Test
	public void withASelectionAStopReturnsToWhereItsFirstPlayBegan() throws MidiPlayerException {
		player.setSelection(fixture.beat(2), fixture.beat(3));

		assertEquals(fixture.start(2), stopAfterPlayingFrom(5000, fixture.start(3)));
	}

	@Test
	public void anIncompleteSelectionPlaysAsNoSelection() throws MidiPlayerException {
		player.setSelection(fixture.beat(2), null);

		assertEquals(6000, stopAfterPlayingFrom(5000, 6000));
	}

	@Test
	public void aFailingSequencerStopsThePlayerAndIsReported() {
		fixture.sequencer.broken = true;
		player.setRunning(true);

		MidiPlayerException failure = assertThrows(MidiPlayerException.class, () -> player.play());
		assertEquals("cannot check, running false, 0 started, 0 looped",
				failure.getMessage() + ", running " + player.isRunning() + ", " + threads.pending());
	}
}
