package app.tuxguitar.audio;

/**
 * The input level of an audio track: the peak of what its input channel(s) delivered in the last second, in dBFS (the
 * higher of the two sides for a pair). At -0.1 dBFS or above the input is clipping; it shows it has clipped until the
 * clip indicator is reset.
 */
public final class TGInputMeter {

	// -0.1 dBFS
	private static final double CLIPPING = Math.pow(10, -0.1 / 20);

	// the peak of each of the last second's samples, the oldest overwritten first
	private final float[] lastSecond;
	private int next;
	private boolean clipped;

	public TGInputMeter(int sampleRate) {
		this.lastSecond = new float[sampleRate];
	}

	// a block of what the input delivered, one array per side, all of the same length
	public void deliver(float[][] sides) {
		for (int i = 0; i < sides[0].length; i++) {
			float peak = 0;
			for (float[] side : sides) {
				peak = Math.max(peak, Math.abs(side[i]));
			}
			this.lastSecond[this.next] = peak;
			this.next = (this.next + 1) % this.lastSecond.length;
			this.clipped |= (peak >= CLIPPING);
		}
	}

	private float peak() {
		float peak = 0;
		for (float sample : this.lastSecond) {
			peak = Math.max(peak, sample);
		}
		return peak;
	}

	// minus infinity for silence
	public double getLevel() {
		return 20 * Math.log10(this.peak());
	}

	public boolean isClipping() {
		return this.peak() >= CLIPPING;
	}

	public boolean hasClipped() {
		return this.clipped;
	}

	public void resetClipIndicator() {
		this.clipped = false;
	}
}
