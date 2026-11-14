package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import app.tuxguitar.audio.device.TGAudioBackend;
import app.tuxguitar.audio.device.TGAudioCallback;
import app.tuxguitar.audio.device.TGAudioDeviceException;
import app.tuxguitar.audio.device.TGAudioDeviceInfo;
import app.tuxguitar.audio.device.TGAudioStream;

/**
 * A double of the native audio backend that needs no hardware: one device, whose stream runs only when a test runs
 * its blocks. Each block, its input channels deliver what the input (a TGAudioDevice, such as the Test Interface)
 * delivers at the stream's clock, and what the callback plays is kept. It reports the latencies it is given.
 */
public final class TestBackend implements TGAudioBackend {

	private final TGAudioDevice input;
	private final int inputLatency;
	private final int outputLatency;
	private List<TGAudioDeviceInfo> devices;
	private int defaultInput;
	private int defaultOutput;
	private Stream stream;

	public TestBackend(TGAudioDevice input, int inputLatency, int outputLatency) {
		this.input = input;
		this.inputLatency = inputLatency;
		this.outputLatency = outputLatency;
		this.list(0, 0, new TGAudioDeviceInfo(0, "Test Interface", "Test", 2, 2, input.getSampleRate()));
	}

	// it lists these devices (each at its index) and defaults instead; a stream opens on any of them, all delivering the
	// same input
	public TestBackend list(int defaultInput, int defaultOutput, TGAudioDeviceInfo... devices) {
		this.devices = Arrays.asList(devices);
		this.defaultInput = defaultInput;
		this.defaultOutput = defaultOutput;
		return this;
	}

	// its output wired back to its input, as a loopback cable would: input channel 1 delivers the left side the callback
	// played, 2 the right, the output latency and the input latency later (silence before)
	public static TestBackend loopback(int sampleRate, int inputLatency, int outputLatency) {
		Loopback loopback = new Loopback(sampleRate, inputLatency + outputLatency);
		loopback.backend = new TestBackend(loopback, inputLatency, outputLatency);
		return loopback.backend;
	}

	private static final class Loopback implements TGAudioDevice {
		private final int sampleRate;
		private final int late;
		private TestBackend backend;

		Loopback(int sampleRate, int late) {
			this.sampleRate = sampleRate;
			this.late = late;
		}

		public int getSampleRate() {
			return this.sampleRate;
		}

		public float[] capture(int inputChannel, long from, int count) {
			float[] samples = new float[count];
			IntStream.range(0, count).forEach(i -> samples[i] = this.backend.stream.playedAt(inputChannel - 1, from + i - this.late));
			return samples;
		}
	}

	// those of the devices it lists, in their order
	public List<String> getHostApis() {
		return this.devices.stream().map(TGAudioDeviceInfo::getHostApi).distinct().collect(Collectors.toList());
	}

	public List<TGAudioDeviceInfo> getDevices() {
		return this.devices;
	}

	public int getDefaultInput() {
		return this.defaultInput;
	}

	public int getDefaultOutput() {
		return this.defaultOutput;
	}

	// an output device it does not list does not open
	public TGAudioStream open(int inputDevice, int inputChannels, int outputDevice, int outputChannels, double sampleRate, int frames, TGAudioCallback callback) throws TGAudioDeviceException {
		if (outputDevice < 0 || outputDevice >= this.devices.size()) {
			throw new TGAudioDeviceException("no output device " + outputDevice);
		}
		this.stream = new Stream(inputChannels, outputChannels, sampleRate, frames, callback);
		this.stream.opened = inputDevice + " x" + inputChannels + " to " + outputDevice + " at " + this.stream.getSampleRate();
		return this.stream;
	}

	// the stream last opened
	public Stream getStream() {
		return this.stream;
	}

	public final class Stream implements TGAudioStream {
		private final TGAudioCallback callback;
		private final float[][] inputs;
		private final float[][] outputs;
		private final List<float[][]> played = new ArrayList<float[][]>();
		private long clock;
		private boolean started;
		private boolean closed;

		// the rate asked for, else the input's
		private final int sampleRate;
		// "<input device> x<input channels> to <output device> at <sample rate>"
		private String opened;

		Stream(int inputChannels, int outputChannels, double sampleRate, int frames, TGAudioCallback callback) {
			this.callback = callback;
			this.inputs = new float[inputChannels][frames];
			this.outputs = new float[outputChannels][frames];
			this.sampleRate = (sampleRate != 0 ? (int) sampleRate : TestBackend.this.input.getSampleRate());
		}

		public int getSampleRate() {
			return this.sampleRate;
		}

		public int getBlock() {
			return this.inputs.length > 0 ? this.inputs[0].length : this.outputs[0].length;
		}

		public int getInputLatency() {
			return TestBackend.this.inputLatency;
		}

		public int getOutputLatency() {
			return TestBackend.this.outputLatency;
		}

		public void start() {
			this.started = true;
		}

		public void close() {
			this.closed = true;
		}

		public boolean isRunning() {
			return this.started && !this.closed;
		}

		// "<input device> x<input channels> to <output device> at <sample rate>"
		public String getOpened() {
			return this.opened;
		}

		// the device asks for the next blocks, as many as given
		public void run(int blocks) {
			IntStream.range(0, blocks).filter(block -> this.isRunning()).forEach(block -> this.block());
		}

		private void block() {
			int frames = this.getBlock();
			IntStream.range(0, this.inputs.length).forEach(c -> this.inputs[c] = TestBackend.this.input.capture(c + 1, this.clock, frames));
			this.callback.process(this.inputs, this.outputs, frames);
			this.played.add(Arrays.stream(this.outputs).map(float[]::clone).toArray(float[][]::new));
			this.clock += frames;
		}

		// what the callback played on the output channel at the frame of the clock; silence where it played nothing
		private float playedAt(int channel, long frame) {
			int block = (int) Math.floorDiv(frame, (long) this.getBlock());
			return (block >= 0 && block < this.played.size() ? this.played.get(block)[channel][(int) (frame % this.getBlock())] : 0f);
		}

		// what the callback played, each output channel from the first block run
		public float[][] getPlayed() {
			float[][] played = new float[this.outputs.length][this.played.size() * this.getBlock()];
			IntStream.range(0, this.played.size()).forEach(block -> TGBlocks.place(this.played.get(block), played, (long) block * this.getBlock()));
			return played;
		}
	}
}
