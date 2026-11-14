package app.tuxguitar.audio.device;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The JNI library over PortAudio, from Java, on this machine's audio devices: PortAudio has no device that needs no
 * hardware, so the stream tests run on the default devices and are skipped, saying so, where the machine has none.
 */
class TestPortAudio {

	private static final int BLOCK = 512;
	private static final int BLOCKS = 20;

	private static TGPortAudio backend;

	@BeforeAll
	static void load() throws TGAudioDeviceException {
		backend = TGPortAudio.load(new File(System.getProperty("tg.audio.native")));
	}

	@AfterAll
	static void unload() {
		backend.close();
	}

	@Test
	void onWindowsItHostsAsioWasapiDirectSoundAndMme() {
		assumeTrue(System.getProperty("os.name").startsWith("Windows"), "not Windows");
		assertEquals(new HashSet<String>(Arrays.asList("ASIO", "Windows WASAPI", "Windows DirectSound", "MME")), new HashSet<String>(backend.getHostApis()));
	}

	@Test
	void eachDeviceIsListedWithItsHostApiChannelsAndSampleRate() {
		List<TGAudioDeviceInfo> devices = backend.getDevices();
		assumeTrue(!devices.isEmpty(), "this machine has no audio device");
		for (int i = 0; i < devices.size(); i++) {
			TGAudioDeviceInfo device = devices.get(i);
			assertEquals(i, device.getIndex());
			assertFalse(device.getName().isEmpty());
			assertTrue(backend.getHostApis().contains(device.getHostApi()), device.toString());
			assertTrue(device.getInputs() + device.getOutputs() > 0, device.toString());
			assertTrue(device.getSampleRate() > 0, device.toString());
		}
	}

	@Test
	void aStreamOnTheDefaultOutputRunsInBlocksAtTheDevicesRateAndReportsItsLatency() throws Exception {
		int output = backend.getDefaultOutput();
		assumeTrue(output != TGAudioBackend.NONE, "this machine has no default output device: the smoke run was not run");
		Blocks blocks = new Blocks();
		TGAudioStream stream = backend.open(TGAudioBackend.NONE, 0, output, 2, 0, BLOCK, blocks);
		try {
			stream.start();
			assertTrue(blocks.awaitAll(), "the device ran " + blocks.frames.size() + " blocks in 5 s");
		} finally {
			stream.close();
		}
		stream.close();
		int run = blocks.count();
		TimeUnit.MILLISECONDS.sleep(100);
		assertEquals(run, blocks.count(), "the callback ran after the stream closed");
		assertEquals((int) backend.getDevices().get(output).getSampleRate(), stream.getSampleRate());
		assertEquals(BLOCK, stream.getBlock());
		assertEquals(new HashSet<Integer>(Arrays.asList(BLOCK)), new HashSet<Integer>(blocks.frames));
		assertEquals(Arrays.asList(0, 2), Arrays.asList(blocks.inputs, blocks.outputs));
		assertTrue(stream.getOutputLatency() > 0, "output latency " + stream.getOutputLatency());
	}

	@Test
	void aStreamOnTheDefaultInputDeliversTheChannelsItOpened() throws Exception {
		int input = backend.getDefaultInput();
		assumeTrue(input != TGAudioBackend.NONE && backend.getDefaultOutput() != TGAudioBackend.NONE, "this machine has no default input and output: the input run was not run");
		Blocks blocks = new Blocks();
		TGAudioStream stream = backend.open(input, 1, backend.getDefaultOutput(), 2, 0, BLOCK, blocks);
		try {
			stream.start();
			assertTrue(blocks.awaitAll(), "the device ran " + blocks.frames.size() + " blocks in 5 s");
		} finally {
			stream.close();
		}
		assertEquals(Arrays.asList(1, 2), Arrays.asList(blocks.inputs, blocks.outputs));
		assertTrue(stream.getInputLatency() > 0, "input latency " + stream.getInputLatency());
	}

	@Test
	void aDeviceThatDoesNotExistIsRefusedWithPortAudiosMessage() {
		TGAudioDeviceException refused = assertThrows(TGAudioDeviceException.class, () -> backend.open(TGAudioBackend.NONE, 0, 9999, 2, 44100, BLOCK, new Blocks()));
		assertEquals("Invalid device", refused.getMessage());
	}

	// the blocks the device asked for, silent
	private static final class Blocks implements TGAudioCallback {
		private final List<Integer> frames = new ArrayList<Integer>();
		private final CountDownLatch all = new CountDownLatch(BLOCKS);
		private int inputs;
		private int outputs;

		public synchronized void process(float[][] input, float[][] output, int frames) {
			this.frames.add(frames);
			this.inputs = input.length;
			this.outputs = output.length;
			for (float[] side : output) {
				Arrays.fill(side, 0, frames, 0f);
			}
			this.all.countDown();
		}

		synchronized int count() {
			return this.frames.size();
		}

		boolean awaitAll() throws InterruptedException {
			return this.all.await(5, TimeUnit.SECONDS);
		}
	}
}
