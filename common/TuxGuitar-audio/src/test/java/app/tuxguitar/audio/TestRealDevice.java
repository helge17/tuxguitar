package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import app.tuxguitar.audio.device.TGAudioBackend;
import app.tuxguitar.audio.device.TGPortAudio;
import app.tuxguitar.midi.synth.TGSynthChannel;
import app.tuxguitar.midi.synth.TGSynthModel;

/**
 * The whole live chain on this machine's real audio devices, through PortAudio. Opt-in: it needs the native library
 * of TuxGuitar-audio-device (-Dtg.audio.native=<its native-build/lib>) and sound hardware, and is skipped, saying so,
 * without either. What it measured is printed.
 */
class TestRealDevice {

	private static final String SKIPPED = "-Dtg.audio.native=<the native library folder> was not given: the real-device smoke run was SKIPPED";

	private static TGPortAudio backend;

	private final LiveSong song = new LiveSong();

	@BeforeAll
	static void load() throws Exception {
		String directory = System.getProperty("tg.audio.native");
		if (directory == null) {
			System.out.println(SKIPPED);
		} else {
			backend = TGPortAudio.load(new File(directory));
		}
	}

	@AfterAll
	static void unload() {
		if (backend != null) {
			backend.close();
		}
	}

	// the backend, unless the run is skipped
	private static TGPortAudio backend() {
		assumeTrue(backend != null, SKIPPED);
		return backend;
	}

	// a General MIDI note from Gervill, through the synthesizer, on the default output for half a second
	@Test
	void aNotePlaysOnTheDefaultOutputUntilTheStreamCloses() throws Exception {
		int output = backend().getDefaultOutput();
		assumeTrue(output != TGAudioBackend.NONE, "this machine has no default output device: the smoke run was not run");
		AtomicLong played = new AtomicLong();
		TGLiveDevice device = TGLiveDevice.open(backend, TGAudioBackend.NONE, 0, output);
		int rate = device.getSampleRate();
		int latency = device.getOutputLatency();
		try {
			device.addListener((clock, frames) -> played.addAndGet(frames));
			TGSynthModel synthesizer = LiveOutput.synthesizer(this.song.context, rate);
			device.play(synthesizer);
			TGSynthChannel channel = (TGSynthChannel) synthesizer.openChannel(0);
			channel.sendProgramChange(0);
			while (synthesizer.isBusy()) {
				Thread.yield();
			}
			channel.sendNoteOn(69, 100, 0, false);
			TimeUnit.MILLISECONDS.sleep(500);
			channel.sendNoteOff(69, 100, 0, false);
		} finally {
			device.close();
		}
		long closed = played.get();
		TimeUnit.MILLISECONDS.sleep(200);
		System.out.println("real device: " + closed + " frames at " + rate + " Hz, output latency " + latency);
		assertTrue(closed >= rate / 2, "the device played " + closed + " frames in half a second");
		assertEquals(closed, played.get(), "the stream ran on after it closed");
	}

	// full duplex on the default input and output: a recording of one measure of the song makes a take of its length
	@Test
	void aLiveRecordingOnTheDefaultInputMakesATakeOfWhatPlayed() throws Exception {
		int input = backend().getDefaultInput();
		assumeTrue(input != TGAudioBackend.NONE && backend.getDefaultOutput() != TGAudioBackend.NONE, "this machine has no default input and output: the recording run was not run");
		try (TGLiveDevice device = TGLiveDevice.open(backend, input, 1, backend.getDefaultOutput())) {
			assertEquals(1, this.song.record(device, 1, 0, () -> TimeUnit.MILLISECONDS.sleep(10)));
			System.out.println("real device recording: input latency " + device.getInputLatency() + ", output latency " + device.getOutputLatency() + ", take of " + this.song.take(1).length + " samples at " + device.getSampleRate() + " Hz");
			assertEquals(LiveSong.length(device.getSampleRate()), this.song.take(1).length);
		}
	}
}
