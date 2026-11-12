package app.tuxguitar.audio.features;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.LongToDoubleFunction;

import app.tuxguitar.audio.TGAudioDevice;
import app.tuxguitar.audio.TGPlayedMeasure;
import app.tuxguitar.audio.TGTimeline;

/**
 * The audio device of the features: 44100 Hz unless a scenario says otherwise; each input channel delivers the signal
 * the player plays on it, or silence. The player plays along with what is being played: it knows where its beats
 * and passes fall on the device's clock. No latency yet.
 */
final class TestInterface implements TGAudioDevice {

	static final int SAMPLE_RATE = 44100;

	private final Map<Integer, LongToDoubleFunction> inputs = new HashMap<Integer, LongToDoubleFunction>();
	private final Set<Long> beats = new HashSet<Long>();
	private final TreeMap<Long, Integer> passes = new TreeMap<Long, Integer>();
	private int sampleRate = SAMPLE_RATE;
	private boolean playing;

	void play(int inputChannel, LongToDoubleFunction signal) {
		this.inputs.put(inputChannel, signal);
	}

	void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public int getSampleRate() {
		return this.sampleRate;
	}

	// what is about to be played: its beats and passes on the device's clock
	void setPlayed(TGTimeline timeline) {
		this.playing = true;
		this.beats.clear();
		this.passes.clear();
		for (int i = 0; i < timeline.count(); i++) {
			TGPlayedMeasure measure = timeline.get(i);
			long beat = measure.getHeader().getTimeSignature().getDenominator().getTime();
			for (long tick = 0; tick < measure.getHeader().getLength(); tick += beat) {
				if (tick >= measure.getFrom() && tick < measure.getTo()) {
					this.beats.add(timeline.getClock() + timeline.sampleAt(i, tick));
				}
			}
			this.passes.putIfAbsent(timeline.getClock() + timeline.start(i), measure.getPass());
		}
	}

	boolean isPlaying() {
		return this.playing;
	}

	boolean isBeat(long clock) {
		return this.beats.contains(clock);
	}

	// the pass being played at the clock, from 0
	int passAt(long clock) {
		Map.Entry<Long, Integer> pass = this.passes.floorEntry(clock);
		return (pass == null ? 0 : pass.getValue());
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
