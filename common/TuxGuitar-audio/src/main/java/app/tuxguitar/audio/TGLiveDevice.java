package app.tuxguitar.audio;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import app.tuxguitar.audio.device.TGAudioBackend;
import app.tuxguitar.audio.device.TGAudioCallback;
import app.tuxguitar.audio.device.TGAudioDeviceException;
import app.tuxguitar.audio.device.TGAudioStream;
import app.tuxguitar.midi.synth.TGAudioBufferProcessor;
import app.tuxguitar.midi.synth.TGSynthModel;

/**
 * The audio device live: a stream of the audio backend on the chosen devices, at the output device's sample rate, in
 * blocks of 512 frames the device asks for on its own thread. Its clock counts the frames since the stream started.
 * Each block it keeps what the input channels delivered (the last KEPT_SECONDS, read back by capture), plays what
 * the TuxGuitar Synthesizer playing through it renders (silence while none does), which can read the block's input
 * already, then tells its listeners. It reports the stream's latencies, and tells the synthesizer the output latency,
 * so the position the player reports is the one the device sounds.
 */
public final class TGLiveDevice implements TGAudioDevice, TGAudioCallback, AutoCloseable {

	static final int KEPT_SECONDS = 30;

	private final TGAudioStream stream;
	// per input channel, a ring of the frames delivered: frame f of the clock at f % its length
	private final float[][] kept;
	private final float[][] block = new float[2][TGBlocks.FRAMES];
	private final List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
	// where the block being played starts, and the frames delivered so far: written by the device's thread only
	private volatile long clock;
	private volatile long delivered;
	private volatile TGAudioBufferProcessor synthesizer;

	private TGLiveDevice(TGAudioBackend backend, int inputDevice, int inputChannels, int outputDevice) throws TGAudioDeviceException {
		this.stream = backend.open(inputDevice, inputChannels, outputDevice, 2, 0, TGBlocks.FRAMES, this);
		this.kept = new float[inputChannels][this.stream.getSampleRate() * KEPT_SECONDS];
	}

	// the stream opened on input channels 1 to inputChannels of the input device and the first two of the output
	// device, and started
	public static TGLiveDevice open(TGAudioBackend backend, int inputDevice, int inputChannels, int outputDevice) throws TGAudioDeviceException {
		TGLiveDevice device = new TGLiveDevice(backend, inputDevice, inputChannels, outputDevice);
		device.stream.start();
		return device;
	}

	public int getSampleRate() {
		return this.stream.getSampleRate();
	}

	public int getInputLatency() {
		return this.stream.getInputLatency();
	}

	// the stream renders blocks of exactly the synthesizer's, so it buffers nothing more
	public int getOutputLatency() {
		return this.stream.getOutputLatency();
	}

	// from now on the synthesizer plays through the device; it renders at the device's sample rate
	public void play(TGSynthModel synthesizer) {
		if (synthesizer.getSampleRate() != this.getSampleRate()) {
			throw new IllegalArgumentException("the synthesizer renders at " + synthesizer.getSampleRate() + " Hz, the device runs at " + this.getSampleRate() + " Hz");
		}
		synthesizer.setOutputLatency(this.getOutputLatency());
		this.synthesizer = new TGAudioBufferProcessor(synthesizer);
	}

	// on the device's thread
	public void process(float[][] input, float[][] output, int frames) {
		this.keep(input, frames);
		this.delivered += frames;
		this.render(output, frames);
		this.listeners.forEach(listener -> listener.played(this.clock, frames));
		this.clock += frames;
	}

	private void keep(float[][] input, int frames) {
		for (int c = 0; c < this.kept.length; c++) {
			for (int i = 0; i < frames; i++) {
				this.kept[c][(int) ((this.delivered + i) % this.kept[c].length)] = input[c][i];
			}
		}
	}

	private void render(float[][] output, int frames) {
		TGAudioBufferProcessor synthesizer = this.synthesizer;
		if (synthesizer != null) {
			synthesizer.process();
			synthesizer.getBuffer().read(this.block);
		}
		for (int side = 0; side < output.length; side++) {
			System.arraycopy(this.block[side], 0, output[side], 0, frames);
		}
	}

	// what the input channel delivered over the frames of the clock from the given one that are still kept; silence for
	// the others
	public float[] capture(int inputChannel, long from, int count) {
		float[] kept = this.kept[inputChannel - 1];
		long delivered = this.delivered;
		float[] samples = new float[count];
		for (int i = 0; i < count; i++) {
			long frame = from + i;
			if (frame < delivered && frame >= delivered - kept.length) {
				samples[i] = kept[(int) (frame % kept.length)];
			}
		}
		return samples;
	}

	public long getClock() {
		return this.clock;
	}

	// from the next block on, until removed
	public void addListener(Listener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		this.listeners.remove(listener);
	}

	public void close() {
		this.stream.close();
	}

	// told each block once it is played, on the device's thread: where on the clock it starts, and its frames
	public interface Listener {
		void played(long clock, int frames);
	}
}
