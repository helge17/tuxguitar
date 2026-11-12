package app.tuxguitar.audio.features;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongToDoubleFunction;

import app.tuxguitar.audio.TGAudioDevice;
import app.tuxguitar.audio.TGPlayedMeasure;
import app.tuxguitar.audio.TGTimeline;

/**
 * The audio device of the features: 44100 Hz unless a scenario says otherwise; each input channel delivers the signal
 * the player plays on it, or silence. The player plays along with what is being played: it knows where its beats
 * and passes fall on the device's clock. No latency yet. Held, it keeps the next capture waiting until released, so a
 * recording can be left under way while a scenario does something else.
 */
final class TestInterface implements TGAudioDevice {

	static final int SAMPLE_RATE = 44100;

	private final Map<Integer, LongToDoubleFunction> inputs = new HashMap<Integer, LongToDoubleFunction>();
	private final Set<Long> beats = new HashSet<Long>();
	private final TreeMap<Long, Integer> passes = new TreeMap<Long, Integer>();
	private int sampleRate = SAMPLE_RATE;
	private boolean playing;
	private final AtomicReference<CountDownLatch> release = new AtomicReference<CountDownLatch>();
	private final CountDownLatch waiting = new CountDownLatch(1);

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

	// the next capture waits until release
	void hold() {
		this.release.set(new CountDownLatch(1));
	}

	// waits until a capture is waiting
	boolean awaitWaiting() throws InterruptedException {
		return this.waiting.await(60, TimeUnit.SECONDS);
	}

	void release() {
		CountDownLatch held = this.release.getAndSet(null);
		if (held != null) {
			held.countDown();
		}
	}

	private void waitIfHeld() {
		CountDownLatch held = this.release.get();
		if (held != null) {
			this.waiting.countDown();
			try {
				held.await(60, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public float[] capture(int inputChannel, long from, int count) {
		this.waitIfHeld();
		LongToDoubleFunction signal = this.inputs.getOrDefault(inputChannel, sample -> 0);
		float[] samples = new float[count];
		for (int i = 0; i < count; i++) {
			samples[i] = (float) signal.applyAsDouble(from + i);
		}
		return samples;
	}
}
