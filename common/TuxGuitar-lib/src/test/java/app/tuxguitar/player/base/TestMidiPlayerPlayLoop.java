package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGDuration;

/**
 * Pins the loop that follows a play (MidiPlayer.runPlayLoopProcess) and how a play ends (MidiPlayer.runPlayEndProcess).
 *
 * While the sequencer runs, each turn of the loop makes a move set meanwhile, then follows the sequencer's position;
 * once it stops, the loop ends the play. A play ended before the last half beat of its sequence stops where it is;
 * one that reached it returns to where it began, unless the loop mode plays the song again (not a selection). The
 * stop is notified; a failure resets the player.
 *
 * The song has 4 measures and plays through a FakeSequencer (see PlayingFixture) whose sequence is 16320 ticks long:
 * its last half beat starts at 15840. A player that has not played yet began at the song start, 960.
 */
public class TestMidiPlayerPlayLoop {

	private static final long LAST_HALF_BEAT = FakeSequencer.SEQUENCE_LENGTH - TGDuration.QUARTER_TIME / 2;

	private final PlayingFixture fixture = new PlayingFixture();
	private final MidiPlayer player = fixture.player;
	private final FakeSequencer sequencer = fixture.sequencer;

	public TestMidiPlayerPlayLoop() {
		player.tickLength = FakeSequencer.SEQUENCE_LENGTH;
	}

	// one turn of the play loop: what it logged | whether the loop goes on | where the player is | whether it runs
	private String loopTurn() {
		boolean goesOn = player.runPlayLoopProcess();
		return fixture.takeLog() + " | " + goesOn + " | " + player.getTickPosition() + " | " + player.isRunning();
	}

	// ends a play that reached the position: what it logged | where the player is | whether it runs
	private String endAt(long position) {
		player.tickPosition = position;
		player.runPlayEndProcess();
		return fixture.takeLog() + " | " + player.getTickPosition() + " | " + player.isRunning();
	}

	// ---- runPlayLoopProcess ----

	@Test
	public void whileTheSequencerRunsTheLoopFollowsItsPositionMakingAMoveSetMeanwhileFirst() {
		sequencer.running = true;
		sequencer.position = 3000;
		String following = loopTurn();
		player.setTickPosition(5000);

		assertEquals(" | true | 3000 | true / seek 5000 | true | 5000 | true", following + " / " + loopTurn());
	}

	@Test
	public void onceTheSequencerStopsTheLoopEndsThePlay() {
		player.setRunning(true);
		player.tickPosition = 3000;

		assertEquals("stopped | false | 3000 | false", loopTurn());
	}

	@Test
	public void aFailingSequencerEndsTheLoopAndResetsThePlayer() {
		sequencer.running = true;
		sequencer.broken = true;

		assertEquals("stop | false | 960 | false", loopTurn());
	}

	// ---- runPlayEndProcess ----

	@Test
	public void aPlayStopsWhereItIsUntilItsLastHalfBeatThenReturnsToWhereItBegan() {
		player.setRunning(true);
		String before = endAt(LAST_HALF_BEAT - 1);
		player.setRunning(true);

		assertEquals("stopped | 15839 | false / stopped | 960 | false", before + " / " + endAt(LAST_HALF_BEAT));
	}

	@Test
	public void aStoppedPlayerOnlyNotifiesTheStop() {
		assertEquals("stopped | 15840 | false", endAt(LAST_HALF_BEAT));
	}

	@Test
	public void theLoopModePlaysTheSongAgainFromItsStartButStopsASelection() {
		player.getMode().setLoop(true);
		player.setRunning(true);
		String song = endAt(LAST_HALF_BEAT) + ", " + fixture.threads.pending();
		player.setSelection(fixture.beat(2), fixture.beat(3));

		assertEquals("loop, seek 960 | 960 | true, 1 started, 0 looped / stopped | 960 | false",
				song + " / " + endAt(LAST_HALF_BEAT));
	}

	@Test
	public void aLoopThatFailsToPlayAgainResetsThePlayer() {
		player.getMode().setLoop(true);
		player.setRunning(true);
		sequencer.broken = true;

		assertEquals("loop | 960 | false", endAt(LAST_HALF_BEAT));
	}
}
