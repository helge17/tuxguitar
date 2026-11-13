package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * A recording live on the live device (TestBackend), of LiveSong: its sequencer starts playing 2 blocks (1024 frames)
 * into the recording. Input channel 1 delivers the ramp of TestLiveDevice: 0.1 + a millionth per frame of the device's
 * clock. The device reports 200 frames of input latency and 300 of output latency; nothing sounds on the synthesizer,
 * so no chain delays what is heard.
 */
class TestLiveRecording {

	private static final int RATE = 48000;
	private static final int LENGTH = LiveSong.length(RATE);

	private final LiveSong song = new LiveSong();
	private final TestBackend backend = new TestBackend(TestLiveDevice.RAMPS, 200, 300);

	// played so many times, the device running on for so many blocks before the recording ends: how many takes it made
	private int record(int times, int on) throws Exception {
		try (TGLiveDevice device = TGLiveDevice.open(this.backend, 0, 2, 0)) {
			return this.song.record(device, times, on, () -> this.backend.getStream().run(1));
		}
	}

	// what the device's input delivered over the frames of its clock
	private static String delivered(long from, int count) {
		return Arrays.toString(TestLiveDevice.RAMPS.capture(1, from, count));
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
		assertEquals(delivered(1024 + 500, 1), Arrays.toString(Arrays.copyOf(this.song.take(1), 1)));
	}

	@Test
	void stoppedBeforeTheSongPlayedItMakesNoTake() throws Exception {
		assertEquals(0, record(0, 0));
		assertEquals(0, this.song.countTakes());
	}
}
