package app.tuxguitar.audio;

import java.util.Map;

import app.tuxguitar.song.models.TGTake;

/**
 * A stretch of the output where an audio track plays one thing without a break: a take from one of its samples to
 * another (time-stretched to the stretch when the tempo differs from the one it was recorded at), its monitored input,
 * or silence. It knows the takes under it, to tell a take that goes on from a take that is cut, and it fades in or out
 * over FADE samples on either side of its bounds as the crossfade rules say.
 */
final class TGTakePiece {

	static final int FADE = 256;

	// a chosen take counts as newer than any take, the monitored input as newer still
	static final int CHOSEN_RANK = Integer.MAX_VALUE - 1;
	static final int INPUT_RANK = Integer.MAX_VALUE;

	final long start;
	final long end;
	final boolean jump;
	final TGTake take;
	final boolean input;
	final int rank;
	final int takeStart;
	final int takeEnd;
	// the takes covering the stretch, with the samples of each that would play there: {start, end}
	final Map<TGTake, int[]> under;
	private final float[] audio;
	private final float[] source;

	boolean fadeIn;
	boolean fadeOut;
	boolean head;
	boolean tail;

	// a take from takeStart to takeEnd of its samples (source), the audio over the stretch given (null when it plays the
	// samples as they are)
	TGTakePiece(long start, long end, boolean jump, TGTake take, int rank, int takeStart, int takeEnd, float[] source, float[] audio, Map<TGTake, int[]> under) {
		this.start = start;
		this.end = end;
		this.jump = jump;
		this.take = take;
		this.input = false;
		this.rank = rank;
		this.takeStart = takeStart;
		this.takeEnd = takeEnd;
		this.source = source;
		this.audio = audio;
		this.under = under;
	}

	// the monitored input (its samples on the output clock), or silence when input is null
	TGTakePiece(long start, long end, boolean jump, float[] input, Map<TGTake, int[]> under) {
		this.start = start;
		this.end = end;
		this.jump = jump;
		this.take = null;
		this.input = (input != null);
		this.rank = INPUT_RANK;
		this.takeStart = 0;
		this.takeEnd = 0;
		this.source = input;
		this.audio = null;
		this.under = under;
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
		return this.input || (this.take != null && this.takeEnd < this.source.length - 1);
	}

	void addTo(float[] tap) {
		long from = Math.max(0, this.start - (this.head ? FADE : 0));
		long to = Math.min(tap.length, this.end + (this.tail ? FADE : 0));
		for (long o = from; o < to; o++) {
			tap[(int) o] += (float) (this.gain(o) * this.sample(o));
		}
	}

	private double gain(long o) {
		if (o < this.start) {
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

	// the audio at the output sample: the stretched audio inside, the take's own samples next to it
	private float sample(long o) {
		if (this.input) {
			return this.source[(int) o];
		}
		if (this.audio != null && o >= this.start && o < this.end) {
			return this.audio[(int) (o - this.start)];
		}
		return this.takeSample(o);
	}

	// the take's own sample there, counted from where it starts or stops; silence outside the take
	private float takeSample(long o) {
		long index = (o < this.end ? this.takeStart + (o - this.start) : this.takeEnd + (o - this.end));
		return (index >= 0 && index < this.source.length ? this.source[(int) index] : 0f);
	}
}
