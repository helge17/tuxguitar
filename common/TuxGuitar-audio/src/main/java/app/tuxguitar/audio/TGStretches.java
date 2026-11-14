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
 * The time-stretches an audio track's taps play, kept from one tap to the next, so a tap built again (live: at each
 * jump back and each time playing starts) finds what the last one made: a stretch is a take's samples from one to
 * another played over a length at the sample rate, the same wherever and in whichever pass of a loop it plays. A tap
 * is built with what the last one was built with; what it does not use is dropped, so they never hold more than one
 * tap's. Used on the thread that builds the taps; a stretch is made on any (Stretch).
 */
final class TGStretches {

	private Map<List<Object>, Stretch> last = new HashMap<List<Object>, Stretch>();
	private Map<List<Object>, Stretch> used = new HashMap<List<Object>, Stretch>();

	// the tap the builder builds, with what the last one was built with; what it does not use is dropped
	<T> T build(Supplier<T> builder) {
		this.last = this.used;
		this.used = new HashMap<List<Object>, Stretch>();
		T built = builder.get();
		this.last = new HashMap<List<Object>, Stretch>();
		return built;
	}

	// the stretch of the take's samples (source) from one to another (at the sample rate) over the length, which the
	// maker makes
	Stretch stretch(TGTake source, int sampleRate, int from, int to, int length, Supplier<float[][]> maker) {
		List<Object> key = Arrays.asList(source, sampleRate, from, to, length);
		return this.used.computeIfAbsent(key, each -> Optional.ofNullable(this.last.get(each)).orElseGet(() -> new Stretch(maker)));
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
