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
import app.tuxguitar.midi.synth.impl.GervillProcessor;

/**
 * The audio device of the features: 44100 Hz unless a scenario says otherwise; each input channel delivers the signal
 * the player plays on it, or silence. The player plays along with what is being heard: it knows where the beats and
 * passes of what is played fall on the device's clock, and hears them the chain delay and the output latency later;
 * what it plays reaches the input the input latency later still. The chain delay is how much later than the clock the
 * TuxGuitar Synthesizer sends to the output what it plays: it delays every track to its slowest sound chain, the
 * General MIDI drums' at least (Gervill's two control periods, 600 samples at 44100 Hz). It reports its input latency
 * (or what a scenario says it reports) and its output latency; none by default. Held, it keeps the next capture waiting until released, so a recording can be left
 * under way while a scenario does something else.
 */
final class TestInterface implements TGAudioDevice {

	static final int SAMPLE_RATE = 44100;
	// the longest a held recording waits for the scenario, and the scenario for it: the steps take milliseconds
	static final int WAIT_SECONDS = 5;

	private final Map<Integer, LongToDoubleFunction> inputs = new HashMap<Integer, LongToDoubleFunction>();
	private final Set<Long> beats = new HashSet<Long>();
	private final TreeMap<Long, Integer> passes = new TreeMap<Long, Integer>();
	private int sampleRate = SAMPLE_RATE;
	private int chainDelay = GervillProcessor.latency(SAMPLE_RATE);
	private int inputLatency;
	private int outputLatency;
	private int reportedInputLatency;
	private boolean playing;
	private final AtomicReference<CountDownLatch> release = new AtomicReference<CountDownLatch>();
	private final CountDownLatch waiting = new CountDownLatch(1);

	void play(int inputChannel, LongToDoubleFunction signal) {
		this.inputs.put(inputChannel, signal);
	}

	void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
		this.chainDelay = GervillProcessor.latency(sampleRate);
	}

	// what the player plays along with is not heard through the TuxGuitar Synthesizer (a take put on a track directly)
	void hearNoChain() {
		this.chainDelay = 0;
	}

	// how much later than the clock the TuxGuitar Synthesizer sends to the output what it plays
	void setChainDelay(int chainDelay) {
		this.chainDelay = chainDelay;
	}

	int getChainDelay() {
		return this.chainDelay;
	}

	public int getSampleRate() {
		return this.sampleRate;
	}

	// the input latency is reported as it is, until reportInputLatency says otherwise
	void setLatency(int input, int output) {
		this.inputLatency = input;
		this.outputLatency = output;
		this.reportedInputLatency = input;
	}

	void reportInputLatency(int reported) {
		this.reportedInputLatency = reported;
	}

	public int getInputLatency() {
		return this.reportedInputLatency;
	}

	public int getOutputLatency() {
		return this.outputLatency;
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

	// waits until a capture is waiting, or the recording has ended without one: a recording reaches the device in
	// milliseconds, so the wait gives up after a few seconds
	boolean awaitWaiting(Thread recording) throws InterruptedException {
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(WAIT_SECONDS);
		while (recording.isAlive() && System.nanoTime() < deadline) {
			if (this.waiting.await(10, TimeUnit.MILLISECONDS)) {
				return true;
			}
		}
		return (this.waiting.getCount() == 0);
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
				held.await(WAIT_SECONDS, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public float[] capture(int inputChannel, long from, int count) {
		this.waitIfHeld();
		LongToDoubleFunction signal = this.inputs.getOrDefault(inputChannel, sample -> 0);
		long played = from - this.chainDelay - this.outputLatency - this.inputLatency;
		float[] samples = new float[count];
		for (int i = 0; i < count; i++) {
			samples[i] = (float) signal.applyAsDouble(played + i);
		}
		return samples;
	}
}
