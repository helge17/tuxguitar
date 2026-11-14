package app.tuxguitar.vst3;

/**
 * What the song is doing at a block's first sample, as a plugin is told: whether it plays, the tempo it sounds at (bpm,
 * the Percentage of Tempo included), the time signature, the musical position in quarter notes, and the position in
 * samples.
 */
public final class TGVst3Transport {

	public static final TGVst3Transport STOPPED = new TGVst3Transport(false, 120, 4, 4, 0, 0);

	private final boolean playing;
	private final double tempo;
	private final int numerator;
	private final int denominator;
	private final double quarters;
	private final long samples;

	public TGVst3Transport(boolean playing, double tempo, int numerator, int denominator, double quarters, long samples) {
		this.playing = playing;
		this.tempo = tempo;
		this.numerator = numerator;
		this.denominator = denominator;
		this.quarters = quarters;
		this.samples = samples;
	}

	public boolean isPlaying() {
		return this.playing;
	}

	public double getTempo() {
		return this.tempo;
	}

	public int getNumerator() {
		return this.numerator;
	}

	public int getDenominator() {
		return this.denominator;
	}

	public double getQuarters() {
		return this.quarters;
	}

	public long getSamples() {
		return this.samples;
	}
}
