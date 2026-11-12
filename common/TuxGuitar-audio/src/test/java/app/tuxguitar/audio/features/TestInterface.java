package app.tuxguitar.audio.features;

import java.util.HashMap;
import java.util.Map;
import java.util.function.LongToDoubleFunction;

import app.tuxguitar.audio.TGAudioDevice;

/**
 * The audio device of the features: 44100 Hz; each input channel delivers the signal the player plays on it, or
 * silence. No latency yet.
 */
final class TestInterface implements TGAudioDevice {

	static final int SAMPLE_RATE = 44100;

	private final Map<Integer, LongToDoubleFunction> inputs = new HashMap<Integer, LongToDoubleFunction>();

	void play(int inputChannel, LongToDoubleFunction signal) {
		this.inputs.put(inputChannel, signal);
	}

	public int getSampleRate() {
		return SAMPLE_RATE;
	}

	public float[] capture(int inputChannel, long from, int count) {
		LongToDoubleFunction signal = this.inputs.getOrDefault(inputChannel, sample -> 0);
		float[] samples = new float[count];
		for (int i = 0; i < count; i++) {
			samples[i] = (float) signal.applyAsDouble(from + i);
		}
		return samples;
	}
}
