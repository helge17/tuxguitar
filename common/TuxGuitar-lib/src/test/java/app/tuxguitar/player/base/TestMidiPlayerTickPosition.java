package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGBeat;

/**
 * Pins how the player moves its sequencer to the position to play from (MidiPlayer.changeTickPosition) and how it
 * starts playing once the synthesizer is ready (MidiPlayer.runPlayStartProcess).
 *
 * A running player moves the sequencer to its position: to the first measure of a selection made since the last
 * move, else not before the loop start; a stopped player leaves it. Starting notifies the start (when asked to),
 * plays the count down between its own notifications, moves the sequencer when a new position was set, starts it,
 * then reads back where the sequencer is; on a failure the player resets.
 *
 * The song has 4 measures; no thread handler is installed, so the play loop that follows does not run. The sequencer
 * logs "start", "stop" and "seek position"; the player's notifications are logged by name.
 */
public class TestMidiPlayerTickPosition {

	private static final long SEQUENCE_LENGTH = 16320;

	private final PlayerSongFixture fixture = new PlayerSongFixture();
	private final MidiPlayer player = fixture.player;
	private final FakeSequencer sequencer = new FakeSequencer();
	private final List<String> log = new ArrayList<String>();

	private class FakeSequencer implements MidiSequencer {
		private long position = 1234;
		private boolean running = false;
		private boolean broken = false;

		public String getKey() { return "fake"; }
		public String getName() { return "fake"; }
		public void open() { }
		public void close() { }
		public void check() { }
		public void setTransmitter(MidiTransmitter transmitter) { }
		public MidiSequenceHandler createSequence(int tracks) { return null; }
		public void setSolo(int index, boolean solo) { }
		public void setMute(int index, boolean mute) { }
		public boolean isRunning() { return this.running; }
		public long getTickPosition() { return this.position; }
		public long getTickLength() { return SEQUENCE_LENGTH; }

		public void start() throws MidiPlayerException {
			fail("start");
			log.add("start");
		}

		public void stop() {
			log.add("stop");
			this.running = false;
		}

		public void setTickPosition(long tickPosition) throws MidiPlayerException {
			fail("seek");
			log.add("seek " + tickPosition);
			this.position = tickPosition;
		}

		private void fail(String action) throws MidiPlayerException {
			if (this.broken) {
				throw new MidiPlayerException("cannot " + action);
			}
		}
	}

	private static final String[] NOTIFICATIONS = {"", "started", "stopped", "count down started", "count down stopped", "loop"};

	public TestMidiPlayerTickPosition() {
		player.loadSequencer(sequencer);
		player.addListener(event -> {
			int type = (Integer) event.getAttribute(MidiPlayerEvent.PROPERTY_NOTIFICATION_TYPE);
			log.add(NOTIFICATIONS[type]);
		});
	}

	// ---- changeTickPosition ----

	@Test
	public void aStoppedPlayerLeavesTheSequencerWhereItIs() {
		assertEquals(" -> 5000", change(5000));
	}

	@Test
	public void aRunningPlayerMovesTheSequencerToItsPosition() {
		player.setRunning(true);

		assertEquals("seek 5000 -> 5000", change(5000));
	}

	@Test
	public void aRunningSequencerIsMovedEvenWhenThePlayerIsNotRunning() {
		sequencer.running = true;

		assertEquals("seek 5000 -> 5000", change(5000));
	}

	@Test
	public void theSequencerIsNotMovedBeforeTheLoopStart() {
		fixture.loopMode(2, 3);
		player.updateLoop(true);
		player.setRunning(true);

		assertEquals("seek " + start(2) + " -> " + start(2) + " | seek " + start(3) + " -> " + start(3),
			change(start(2) - 1) + " | " + change(start(3)));
	}

	@Test
	public void aNewSelectionPlaysFromItsFirstMeasureOnce() {
		player.setSelection(beat(3), beat(4));
		player.updateLoop(true);
		player.setRunning(true);
		long later = start(4) + 100;

		assertEquals("seek " + start(3) + " -> " + start(3) + " | seek " + later + " -> " + later,
			change(later) + " | " + change(later));
	}

	@Test
	public void anIncompleteSelectionKeepsThePosition() {
		player.setSelection(beat(3), null);
		player.setRunning(true);

		assertEquals("seek 5000 -> 5000", change(5000));
	}

	@Test
	public void aSequencerFailureLeavesTheMovePending() {
		player.setRunning(true);
		sequencer.broken = true;
		player.setTickPosition(5000);
		player.changeTickPosition();

		assertEquals("5000 pending", player.getTickPosition() + pending());
	}

	// ---- runPlayStartProcess ----

	@Test
	public void startingNotifiesWhenAskedThenMovesTheSequencerToANewPositionAndStartsIt() {
		player.setRunning(true);
		player.setTickPosition(5000);
		String first = playStart(true);
		player.setTickPosition(6000);
		String restart = playStart(false);

		assertEquals("started, seek 5000, start -> 5000 of " + SEQUENCE_LENGTH + " running"
			+ " | seek 6000, start -> 6000 of " + SEQUENCE_LENGTH + " running"
			+ " | start -> 6000 of " + SEQUENCE_LENGTH + " running",
			first + " | " + restart + " | " + playStart(false));
	}

	@Test
	public void theCountDownPlaysBeforeTheSequencerStarts() {
		player.setRunning(true);
		player.getCountDown().setEnabled(true);

		assertEquals("count down started, count down stopped, start -> 1234 of " + SEQUENCE_LENGTH + " running",
			playStart(false));
	}

	@Test
	public void aStoppedPlayerOnlyReadsTheSequencerBack() {
		player.setTickPosition(5000);

		assertEquals("started -> 1234 of " + SEQUENCE_LENGTH + " pending", playStart(true));
	}

	@Test
	public void aSequencerFailureResetsThePlayer() {
		player.setRunning(true);
		player.setTickPosition(5000);
		sequencer.broken = true;

		assertEquals("started -> 960 of 0", playStart(true));
	}

	// ---- helpers ----

	// sets the position, moves the sequencer, then the position the player is at
	private String change(long position) {
		log.clear();
		player.setTickPosition(position);
		player.changeTickPosition();
		return String.join(", ", log) + " -> " + player.getTickPosition() + pending();
	}

	// starts playing, then the position and length the player read, and whether it is running or still has a move pending
	private String playStart(boolean notifyStarted) {
		log.clear();
		player.runPlayStartProcess(notifyStarted);
		return String.join(", ", log) + " -> " + player.getTickPosition() + " of " + player.tickLength + running() + pending();
	}

	private String running() {
		return (player.isRunning() ? " running" : "");
	}

	// whether a move of the sequencer is still to be made
	private String pending() {
		return (player.isChangeTickPosition() ? " pending" : "");
	}

	private TGBeat beat(int measureNumber) {
		return fixture.beat(measureNumber);
	}

	private long start(int headerNumber) {
		return fixture.start(headerNumber);
	}
}
