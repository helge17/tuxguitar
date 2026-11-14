package app.tuxguitar.audio;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import app.tuxguitar.song.models.TGTake;

/**
 * The time-stretches an audio track's taps play, and the samples of its takes at the sample rate, kept from one tap
 * to the next, so a tap built again (live: at each jump back and each time playing starts) finds what the last one
 * made: a stretch is a take's samples from one to another played over a length at the sample rate, the same wherever
 * and in whichever pass of a loop it plays. A tap is built with what the last one was built with; what it does not use
 * is dropped, so they never hold more than one tap's. Used on the thread that builds the taps; a stretch is made on
 * any (Stretch).
 */
final class TGStretches {

	private final Kept<Stretch> stretches = new Kept<Stretch>();
	private final Kept<float[][]> samples = new Kept<float[][]>();

	// the tap the builder builds, with what the last one was built with; what it does not use is dropped
	<T> T build(Supplier<T> builder) {
		this.stretches.renew();
		this.samples.renew();
		T built = builder.get();
		this.stretches.dropLast();
		this.samples.dropLast();
		return built;
	}

	// the stretch of the take's samples (source) from one to another (at the sample rate) over the length, which the
	// maker makes
	Stretch stretch(TGTake source, int sampleRate, int from, int to, int length, Supplier<float[][]> maker) {
		return this.stretches.find(Arrays.asList(source, sampleRate, from, to, length), () -> new Stretch(maker));
	}

	// one array per side of the take, at the sample rate (resampled when it was recorded at another)
	float[][] samples(TGTake take, int sampleRate) {
		return this.samples.find(Arrays.asList(take, sampleRate), () -> TGResampler.resample(samplesOf(take), take.getSampleRate(), sampleRate));
	}

	private static float[][] samplesOf(TGTake take) {
		float[][] samples = new float[take.countChannels()][take.countSamples()];
		for (int side = 0; side < samples.length; side++) {
			for (int i = 0; i < samples[side].length; i++) {
				samples[side][i] = take.getSample(side, i);
			}
		}
		return samples;
	}

	// what the tap being built uses, found in what the last one used or made
	private static final class Kept<V> {

		private Map<List<Object>, V> last = new HashMap<List<Object>, V>();
		private Map<List<Object>, V> used = new HashMap<List<Object>, V>();

		void renew() {
			this.last = this.used;
			this.used = new HashMap<List<Object>, V>();
		}

		void dropLast() {
			this.last = new HashMap<List<Object>, V>();
		}

		V find(List<Object> key, Supplier<V> maker) {
			return this.used.computeIfAbsent(key, each -> Optional.ofNullable(this.last.get(each)).orElseGet(maker));
		}
	}

	/**
	 * A stretch's audio, made by the first thread to ask (live, the worker ahead of the audio thread, else the audio
	 * thread when a block reaches it); should two make it at once, the first one made is kept.
	 */
	static final class Stretch implements Supplier<float[][]> {

		private final Supplier<float[][]> maker;
		private final AtomicReference<float[][]> made = new AtomicReference<float[][]>();

		Stretch(Supplier<float[][]> maker) {
			this.maker = maker;
		}

		// its audio, made unless it was, on the thread that asks
		public float[][] get() {
			if (this.made.get() == null) {
				this.made.compareAndSet(null, this.maker.get());
			}
			return this.made.get();
		}
	}
}
