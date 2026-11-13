package app.tuxguitar.audio;

/**
 * What a monitored input delivered, as a tap reads it: its sides (one for a mono input, heard on both sides of the
 * tap; left and right for a pair) from a sample of the tap on. Offline the sides cover the whole timeline, from its
 * first sample; live they are the block the device delivered last, from the tap sample that block plays, and are set
 * again each block (on the audio thread that reads them).
 */
final class TGTapInput {

	private long base;
	private float[][] sides;

	TGTapInput(long base, float[][] sides) {
		this.set(base, sides);
	}

	// the sides, from the base sample of the tap on
	void set(long base, float[][] sides) {
		this.base = base;
		this.sides = sides;
	}

	// the input's sample on the tap's side at the tap's sample: a mono input's one side on both
	float at(int side, long sample) {
		return this.sides[Math.min(side, this.sides.length - 1)][(int) (sample - this.base)];
	}
}
