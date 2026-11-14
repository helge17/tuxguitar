package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.audio.device.TGAudioDeviceException;
import app.tuxguitar.midi.synth.TGSynthChannel;
import app.tuxguitar.midi.synth.TGSynthModel;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGPropertiesManager;

/**
 * The audio device live, on a double of the native backend (TestBackend): what it plays, what it keeps of the input,
 * the latencies it reports and tells the synthesizer.
 */
class TestLiveDevice {

	private static final int RATE = 48000;

	private final TGContext context = new TGContext();

	TestLiveDevice() {
		TGPropertiesManager.getInstance(this.context).setPropertiesFactory(MapProperties::new);
	}

	// each input channel delivers its number tenths, rising by a millionth a sample of the clock
	static final TGAudioDevice RAMPS = new SilentDevice() {
		public int getSampleRate() {
			return RATE;
		}

		public float[] capture(int inputChannel, long from, int count) {
			float[] samples = new float[count];
			for (int i = 0; i < count; i++) {
				samples[i] = inputChannel / 10f + (from + i) / 1000000f;
			}
			return samples;
		}
	};

	private static TGLiveDevice open(TestBackend backend) throws TGAudioDeviceException {
		return TGLiveDevice.open(backend, 0, 2, 0);
	}

	@Test
	void itRunsAtTheDevicesRateAndReportsTheStreamsLatencies() throws TGAudioDeviceException {
		try (TGLiveDevice device = open(new TestBackend(RAMPS, 200, 300))) {
			assertEquals(RATE, device.getSampleRate());
			assertEquals(200, device.getInputLatency());
			assertEquals(300, device.getOutputLatency());
		}
	}

	@Test
	void theRoundTripTakesTheLatenciesTheStreamReports() throws TGAudioDeviceException {
		TGAudioSession session = TGAudioSession.getInstance(this.context);
		try (TGLiveDevice device = open(new TestBackend(RAMPS, 200, 300))) {
			session.setDevice(device);
			session.setRecordingOffset(7);
			assertEquals(300 + 600 + 200 + 7, session.getRoundTrip(600));
		}
	}

	@Test
	void theSynthesizerPlayingThroughItIsToldTheOutputLatency() throws TGAudioDeviceException {
		TGSynthModel synthesizer = LiveOutput.synthesizer(this.context, RATE);
		try (TGLiveDevice device = open(new TestBackend(RAMPS, 200, 300))) {
			device.play(synthesizer);
			assertEquals(300, synthesizer.getSoundingDelay() - synthesizer.getLatency());
		}
	}

	@Test
	void aSynthesizerAtAnotherRateIsRefused() throws TGAudioDeviceException {
		try (TGLiveDevice device = open(new TestBackend(RAMPS, 0, 0))) {
			assertThrows(IllegalArgumentException.class, () -> device.play(LiveOutput.synthesizer(this.context, 44100)));
		}
	}

	// an A4 on channel 0, General MIDI program 0, once the synthesizer has loaded it
	private static void soundANote(TGSynthModel synthesizer) throws MidiPlayerException {
		TGSynthChannel channel = (TGSynthChannel) synthesizer.openChannel(0);
		channel.sendProgramChange(0);
		while (synthesizer.isBusy()) {
			Thread.yield();
		}
		channel.sendNoteOn(69, 100, 0, false);
	}

	@Test
	void eachBlockTheDeviceAsksForPlaysTheNextBlockTheSynthesizerRenders()throws TGAudioDeviceException, MidiPlayerException {
		TestBackend backend = new TestBackend(RAMPS, 0, 0);
		TGSynthModel synthesizer = LiveOutput.synthesizer(this.context, RATE);
		int[] frames = new int[1];
		synthesizer.setListener((clock, count) -> frames[0] += count);
		try (TGLiveDevice device = open(backend)) {
			backend.getStream().run(2);
			device.play(synthesizer);
			soundANote(synthesizer);
			backend.getStream().run(20);
		}
		float[][] played = backend.getStream().getPlayed();
		assertEquals(20 * TGBlocks.FRAMES, frames[0]);
		assertEquals(0f, Meters.peak(played[0], 0, 2 * TGBlocks.FRAMES));
		assertTrue(Meters.peak(played[0], 2 * TGBlocks.FRAMES, 22 * TGBlocks.FRAMES) > 0.01, "the note was not played");
		assertTrue(Meters.peak(played[1], 2 * TGBlocks.FRAMES, 22 * TGBlocks.FRAMES) > 0.01, "the note was not played on the right");
	}

	@Test
	void itRunsAtTheSampleRateAskedFor() throws TGAudioDeviceException {
		try (TGLiveDevice device = TGLiveDevice.open(new TestBackend(RAMPS, 0, 0), 0, 2, 0, 44100)) {
			assertEquals(44100, device.getSampleRate());
		}
	}

	// a note sounds for 2 blocks, then the synthesizer stops playing through the device (stopping another one changes
	// nothing): the device plays silence, not its last block again
	@Test
	void aSynthesizerStoppedPlaysThroughItNoMore() throws TGAudioDeviceException, MidiPlayerException {
		TestBackend backend = new TestBackend(RAMPS, 0, 0);
		TGSynthModel synthesizer = LiveOutput.synthesizer(this.context, RATE);
		try (TGLiveDevice device = open(backend)) {
			device.play(synthesizer);
			soundANote(synthesizer);
			backend.getStream().run(2);
			device.stop(LiveOutput.synthesizer(this.context, RATE));
			backend.getStream().run(2);
			device.stop(synthesizer);
			backend.getStream().run(2);
		}
		float[][] played = backend.getStream().getPlayed();
		assertTrue(Meters.peak(played[0], 2 * TGBlocks.FRAMES, 4 * TGBlocks.FRAMES) > 0.01, "the note stopped with another synthesizer");
		assertEquals(0f, Meters.peak(played[0], 4 * TGBlocks.FRAMES, 6 * TGBlocks.FRAMES) + Meters.peak(played[1], 4 * TGBlocks.FRAMES, 6 * TGBlocks.FRAMES));
	}

	@Test
	void itKeepsWhatEachInputChannelDeliveredOnItsClock() throws TGAudioDeviceException {
		TestBackend backend = new TestBackend(RAMPS, 0, 0);
		try (TGLiveDevice device = open(backend)) {
			backend.getStream().run(3);
			assertArrayEquals(RAMPS.capture(2, 1000, 3), device.capture(2, 1000, 3));
			assertArrayEquals(RAMPS.capture(1, 1534, 2), device.capture(1, 1534, 2));
			assertArrayEquals(RAMPS.capture(2, 511, 2), device.capture(2, 511, 2));
		}
	}

	@Test
	void itKeepsTheLastThirtySecondsOfInput() throws TGAudioDeviceException {
		TestBackend backend = new TestBackend(RAMPS, 0, 0);
		int kept = RATE * TGLiveDevice.KEPT_SECONDS;
		try (TGLiveDevice device = open(backend)) {
			backend.getStream().run(kept / TGBlocks.FRAMES + 2);
			long clock = (kept / TGBlocks.FRAMES + 2) * (long) TGBlocks.FRAMES;
			assertArrayEquals(new float[] {0f, RAMPS.capture(1, clock - kept, 1)[0]}, device.capture(1, clock - kept - 1, 2));
			assertArrayEquals(new float[] {RAMPS.capture(1, clock - 1, 1)[0], 0f}, device.capture(1, clock - 1, 2));
		}
	}

	@Test
	void whatTheInputHasNotDeliveredYetIsSilence() throws TGAudioDeviceException {
		TestBackend backend = new TestBackend(RAMPS, 0, 0);
		try (TGLiveDevice device = open(backend)) {
			backend.getStream().run(1);
			assertArrayEquals(new float[] {RAMPS.capture(1, 511, 1)[0], 0f}, device.capture(1, 511, 2));
		}
	}

	// what the device delivered over the block of the clock, as read while rendering it or after: "<clock> <frames> <the
	// first and last frames read as the input delivered them>"
	private static String read(TGLiveDevice device, String when, int frames) {
		long clock = device.getClock();
		boolean first = device.capture(2, clock, 1)[0] == RAMPS.capture(2, clock, 1)[0];
		boolean last = device.capture(1, clock + frames - 1, 1)[0] == RAMPS.capture(1, clock + frames - 1, 1)[0];
		return when + " " + clock + " " + frames + " " + first + " " + last;
	}

	// a source reads the block's input while the synthesizer renders it; a listener is told the block after
	@Test
	void whileABlockIsRenderedAndAfterItsInputIsReadWhereTheClockIs() throws TGAudioDeviceException {
		TestBackend backend = new TestBackend(RAMPS, 0, 0);
		TGSynthModel synthesizer = LiveOutput.synthesizer(this.context, RATE);
		List<String> read = new ArrayList<String>();
		try (TGLiveDevice device = open(backend)) {
			synthesizer.setListener((clock, frames) -> read.add(read(device, "rendering", frames)));
			device.play(synthesizer);
			device.addListener((clock, frames) -> read.add(read(device, "told " + clock, frames)));
			backend.getStream().run(2);
		}
		assertEquals(Arrays.asList("rendering 0 512 true true", "told 0 0 512 true true", "rendering 512 512 true true", "told 512 512 512 true true"), read);
	}

	@Test
	void aListenerRemovedIsToldNoMore() throws TGAudioDeviceException {
		TestBackend backend = new TestBackend(RAMPS, 0, 0);
		List<Long> told = new ArrayList<Long>();
		try (TGLiveDevice device = open(backend)) {
			TGLiveDevice.Listener listener = (clock, frames) -> told.add(clock);
			device.addListener(listener);
			backend.getStream().run(1);
			device.removeListener(listener);
			backend.getStream().run(1);
		}
		assertEquals(Arrays.asList(0L), told);
	}

	@Test
	void closedTheStreamRunsNoMore() throws TGAudioDeviceException {
		TestBackend backend = new TestBackend(RAMPS, 0, 0);
		open(backend).close();
		assertFalse(backend.getStream().isRunning());
	}
}
