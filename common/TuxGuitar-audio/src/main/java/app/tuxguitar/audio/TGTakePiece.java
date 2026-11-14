package app.tuxguitar.audio;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import app.tuxguitar.song.models.TGTake;

/**
 * A stretch of the output where an audio track plays one thing without a break: a take from one of its samples to
 * another (time-stretched to the stretch when the tempo differs from the one it was recorded at), its monitored input,
 * or silence. It knows the takes under it, to tell a take that goes on from a take that is cut, and it fades in or out
 * over FADE samples on either side of its bounds as the crossfade rules say. Its audio has one side (mono, heard on
 * both sides of the tap) or two (left and right).
 */
final class TGTakePiece {

	static final int FADE = 256;

	// how new what a piece plays is: a take by its number; a take chosen for its measure counts as newer than any take
	// not chosen (its rank is CHOSEN_RANK + its number, so between two chosen takes the one recorded later is newer),
	// the monitored input as newer still
	static final int CHOSEN_RANK = 1 << 30;
	static final int INPUT_RANK = Integer.MAX_VALUE;

	final long start;
	final long end;
	final boolean jump;
	final TGTake take;
	final boolean chosen;
	final boolean input;
	final int rank;
	final int takeStart;
	final int takeEnd;
	// the takes covering the stretch, with the samples of each that would play there: {start, end}
	final Map<TGTake, int[]> under;
	// what makes the stretched audio (null when it plays the samples as they are), and that audio once made: by the
	// first thread to ask (live, a worker ahead of the audio thread, else the audio thread when a block reaches it);
	// should two make it at once, the first one made is kept
	private final Supplier<float[][]> stretch;
	private final AtomicReference<float[][]> audio = new AtomicReference<float[][]>();
	private final float[][] source;
	private final TGTapInput monitored;

	boolean fadeIn;
	boolean fadeOut;
	boolean head;
	boolean tail;
	// it sounds before the tap's first sample too (the monitored input the tap starts with)
	boolean lead;

	// a take (chosen for its measure or not) from takeStart to takeEnd of its samples (source), the audio over the
	// stretch made by the supplier when it first sounds (null when it plays the samples as they are)
	TGTakePiece(long start, long end, boolean jump, TGTake take, boolean chosen, int takeStart, int takeEnd, float[][] source, Supplier<float[][]> audio, Map<TGTake, int[]> under) {
		this.start = start;
		this.end = end;
		this.jump = jump;
		this.take = take;
		this.chosen = chosen;
		this.input = false;
		this.rank = rank(take, chosen);
		this.takeStart = takeStart;
		this.takeEnd = takeEnd;
		this.source = source;
		this.monitored = null;
		this.stretch = audio;
		this.under = under;
	}

	// the monitored input (its samples on the output clock), or silence when input is null
	TGTakePiece(long start, long end, boolean jump, TGTapInput input, Map<TGTake, int[]> under) {
		this.start = start;
		this.end = end;
		this.jump = jump;
		this.take = null;
		this.chosen = false;
		this.input = (input != null);
		this.rank = INPUT_RANK;
		this.takeStart = 0;
		this.takeEnd = 0;
		this.source = null;
		this.monitored = input;
		this.stretch = null;
		this.under = under;
	}

	private static int rank(TGTake take, boolean chosen) {
		return (chosen ? CHOSEN_RANK + take.getNumber() : take.getNumber());
	}

	boolean sounds() {
		return (this.take != null || this.input);
	}


	// the next one goes on with the same audio, without a jump: the same take from where this one stops, or the input
	boolean playsOnInto(TGTakePiece next) {
		return !next.jump && ((this.input && next.input) || (this.take != null && this.take == next.take && this.source == next.source && Math.abs(this.takeEnd - next.takeStart) <= 1));
	}

	// this one's audio goes on under the next one
	boolean goesOnUnder(TGTakePiece next) {
		return this.input || covers(next, this.take, this.takeEnd, 0);
	}

	// the next one's audio was already going on under this one
	boolean wasGoingOnUnder(TGTakePiece next) {
		return next.input || covers(this, next.take, next.takeStart, 1);
	}

	private static boolean covers(TGTakePiece piece, TGTake take, int sample, int bound) {
		int[] samples = piece.under.get(take);
		return (samples != null && Math.abs(samples[bound] - sample) <= 1);
	}

	// the take has audio past where this one stops: stopping here cuts it
	boolean hasMore() {
		return this.input || (this.take != null && this.takeEnd < this.source[0].length - 1);
	}

	// adds what it sounds over the frames of the block from the offset, from the sample of the tap the offset is on
	// (before the tap's first sample only a lead sounds), to each side of the block: a mono piece's one side to both
	void addTo(float[][] block, int offset, int frames, long from) {
		long first = Math.max(from, this.earliest());
		long last = Math.min(from + frames, this.end + (this.tail ? FADE : 0));
		for (int side = 0; side < block.length; side++) {
			for (long o = first; o < last; o++) {
				block[side][offset + (int) (o - from)] += (float) (this.gain(o) * this.sample(side, o));
			}
		}
	}

	// the first sample of the tap it sounds on: a lead from any; else from its start (FADE before, its head), not before
	// the tap's first
	private long earliest() {
		return (this.lead ? Long.MIN_VALUE : Math.max(0, this.start - (this.head ? FADE : 0)));
	}

	private double gain(long o) {
		if (o < this.start && this.head) {
			return (o - (this.start - FADE) + 1) / (double) FADE;
		}
		if (o >= this.end) {
			return 1 - (o - this.end + 1) / (double) FADE;
		}
		double gain = 1;
		if (this.fadeIn && o < this.start + FADE) {
			gain *= (o - this.start + 1) / (double) FADE;
		}
		if (this.fadeOut && o >= this.end - FADE) {
			gain *= 1 - (o - (this.end - FADE) + 1) / (double) FADE;
		}
		return gain;
	}

	// the audio at the output sample on the tap's side: the input's; the stretched audio inside, the take's own samples
	// next to it (a mono take's one side on both)
	private float sample(int side, long o) {
		if (this.input) {
			return this.monitored.at(side, o);
		}
		int own = Math.min(side, this.source.length - 1);
		float[][] audio = this.audio();
		if (audio != null && o >= this.start && o < this.end) {
			return audio[own][(int) (o - this.start)];
		}
		return this.takeSample(this.source[own], o);
	}

	private float[][] audio() {
		this.prepare();
		return this.audio.get();
	}

	// its stretched audio, made unless it was (on the thread that asks)
	void prepare() {
		if (this.stretch != null && this.audio.get() == null) {
			this.audio.compareAndSet(null, this.stretch.get());
		}
	}

	// the take's own sample there, counted from where it starts or stops; silence outside the take
	private float takeSample(float[] samples, long o) {
		long index = (o < this.end ? this.takeStart + (o - this.start) : this.takeEnd + (o - this.end));
		return (index >= 0 && index < samples.length ? samples[(int) index] : 0f);
	}
}
