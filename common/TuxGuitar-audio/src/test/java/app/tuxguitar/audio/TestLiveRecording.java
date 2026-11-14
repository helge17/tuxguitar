package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiPlayer;

/**
 * A recording live on the live device (TestBackend), of LiveSong: unless a test says otherwise its sequencer starts
 * playing 2 blocks (1024 frames) into the recording, which starts at the device's first block. Input channel 1
 * delivers the ramp of TestLiveDevice: 0.1 + a millionth per frame of the device's clock. The device reports 200 frames
 * of input latency and 300 of output latency; nothing sounds on the synthesizer, so no chain delays what is heard.
 */
class TestLiveRecording {

	private static final int RATE = 48000;
	private static final int LENGTH = LiveSong.length(RATE);

	private final LiveSong song = new LiveSong();
	private TestBackend backend = new TestBackend(TestLiveDevice.RAMPS, 200, 300);

	private TGLiveDevice open() throws Exception {
		return TGLiveDevice.open(this.backend, 0, 2, 0);
	}

	private void block() {
		this.backend.getStream().run(1);
	}

	// played so many times, the device running on for so many blocks before the recording ends: how many takes it made
	private int record(int times, int on) throws Exception {
		try (TGLiveDevice device = open()) {
			return this.song.record(device, times, on, this::block);
		}
	}

	// what the device's input delivered over the frames of its clock
	private static String delivered(long from, int count) {
		return Arrays.toString(TestLiveDevice.RAMPS.capture(1, from, count));
	}

	private String take(int number, int from, int to) {
		return Arrays.toString(Arrays.copyOfRange(this.song.take(number), from, to));
	}

	// read one round trip later: 300 + 0 + 200 + 7
	@Test
	void aTakeHoldsTheInputFromWhereTheSongStartedPlayingOneRoundTripLater() throws Exception {
		this.song.session.setRecordingOffset(7);
		assertEquals(1, record(1, 0));
		assertEquals(delivered(1024 + 507, LENGTH), Arrays.toString(this.song.take(1)));
	}

	// the song played again from its start: the transport goes back, another take
	@Test
	void eachStretchPlayedWithoutAJumpIsATake() throws Exception {
		assertEquals(2, record(2, 0));
		assertEquals(delivered(1024 + 500 + LENGTH, LENGTH), Arrays.toString(this.song.take(2)));
	}

	// the device keeps its last 30 seconds of input; a recording keeps all it records
	@Test
	void aTakeIsKeptWhenTheRecordingEndsLater() throws Exception {
		record(1, (TGLiveDevice.KEPT_SECONDS + 1) * RATE / TGBlocks.FRAMES);
		assertEquals(delivered(1024 + 500, 1), take(1, 0, 1));
	}

	@Test
	void stoppedBeforeTheSongPlayedItMakesNoTake() throws Exception {
		assertEquals(0, record(0, 0));
		assertEquals(0, this.song.countTakes());
	}

	// the recording starts 3 blocks into the device's clock, the song from measure 2 (its own clock 96000 frames in) 2
	// blocks later: the take is measure 2's, from the frame where measure 2 started playing
	@Test
	void aTakeIsTheInputFromWhereItsMeasuresPlayedOnTheDevicesClock() throws Exception {
		this.song.idle = 3;
		this.song.from = 2;
		assertEquals(1, record(1, 0));
		assertEquals(delivered(1536 + 1024 + 500, LENGTH / 2), Arrays.toString(this.song.take(1)));
	}

	// the song ends 377 blocks (193024 frames) in; with a round trip of 1024 (300 + 200 + 524) the recording ends once
	// the device has delivered the next 2 blocks, not before
	@Test
	void theRecordingEndsOnceTheDeviceHasDeliveredOneRoundTripPastWhatPlayed() throws Exception {
		this.song.session.setRecordingOffset(524);
		try (TGLiveDevice device = open()) {
			TGLiveRecording recording = this.song.play(device, 1, this::block);
			AtomicInteger takes = new AtomicInteger();
			Thread ending = new Thread(() -> takes.set(this.song.endHere(recording)));
			ending.start();
			assertTrue(isAliveAfter(ending, 200), "the recording ended before the device delivered its run-on");
			block();
			assertTrue(isAliveAfter(ending, 200), "the recording ended a block before the device delivered its run-on");
			block();
			assertFalse(isAliveAfter(ending, 5000), "the recording did not end once the device delivered its run-on");
			assertEquals(1, takes.get());
		}
	}

	private static boolean isAliveAfter(Thread thread, long millis) throws InterruptedException {
		thread.join(millis);
		return thread.isAlive();
	}

	// stopped waiting for the run-on, a take holds what was kept and silence after; the player is left as it is
	@Test
	void aRecordingInterruptedBeforeItsRunOnKeepsWhatWasDelivered() throws Exception {
		MidiPlayer player = MidiPlayer.getInstance(this.song.context);
		try (TGLiveDevice device = open()) {
			TGLiveRecording recording = this.song.play(device, 1, this::block);
			player.setRunning(true);
			assertEquals("1 true", this.song.endInterrupted(recording) + " " + player.isRunning());
		}
		assertKeptWhatWasDelivered();
	}

	private void assertKeptWhatWasDelivered() {
		assertEquals(delivered(1024 + 500, 1) + " " + Arrays.toString(new float[500]), take(1, 0, 1) + " " + take(1, LENGTH - 500, LENGTH));
	}

	// the device's callbacks stop before the run-on (its stream ended), the player still running: the recording ends
	// by itself, stops the player and keeps what was delivered
	@Test
	void aRecordingWhoseDeviceStopsEndsByItselfKeepingWhatWasDelivered() throws Exception {
		MidiPlayer player = MidiPlayer.getInstance(this.song.context);
		AtomicInteger takes = new AtomicInteger();
		try (TGLiveDevice device = open()) {
			TGLiveRecording recording = this.song.play(device, 1, this::block);
			player.setRunning(true);
			Thread ending = new Thread(() -> takes.set(this.song.endHere(recording)));
			ending.setDaemon(true);
			ending.start();
			assertFalse(isAliveAfter(ending, 5000), "the recording did not end once the device stopped");
		}
		assertEquals("1 false", takes.get() + " " + player.isRunning());
		assertKeptWhatWasDelivered();
	}

	// a recording offset can make the round trip shorter than nothing: before the recording started is silence
	@Test
	void beforeTheRecordingStartedIsSilence() throws Exception {
		this.backend = new TestBackend(TestLiveDevice.RAMPS, 0, 0);
		this.song.session.setRecordingOffset(-10);
		this.song.lead = 0;
		record(1, 0);
		assertEquals(Arrays.toString(new float[10]) + " " + delivered(0, 1), take(1, 0, 10) + " " + take(1, 10, 11));
	}
}
