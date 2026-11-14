package app.tuxguitar.player.base;

/**
 * What the song does over the block a sample clock is about to render, as plugins are told it (VST3's process context):
 * spans, each from a frame of the block, in which the song plays (or not) at a tempo (quarter notes per minute as it
 * sounds, the Percentage of Tempo included) and a time signature, from a musical position (quarter notes from the
 * start of the song) and a song position in samples. A span starts at frame 0 and wherever the song jumps (a repeat, the
 * jump back of a loop) or a measure of another tempo or time signature starts. The clock's owner writes it before each
 * block; the processors read it on the same thread while they process that block. It allocates nothing per block.
 */
public final class MidiTransport {

	// the most spans a block holds; one past them is left out, the last one running on
	public static final int MAX_SPANS = 8;

	private final Span[] spans = new Span[MAX_SPANS];
	private int count;
	private boolean offline;

	public MidiTransport() {
		for (int i = 0; i < MAX_SPANS; i++) {
			this.spans[i] = new Span();
		}
		this.stop();
	}

	// the song does not play over the whole block
	public void stop() {
		this.count = 0;
		this.add(0, false, 120, 4, 4, 0, 0);
	}

	// the block's spans are written again, from its first frame
	public void clear() {
		this.count = 0;
	}

	// a span from the frame on, after the ones added before it; one from the frame the last one starts on replaces it
	public void add(int frame, boolean playing, double tempo, int numerator, int denominator, double quarters, long samples) {
		if (this.count > 0 && this.spans[this.count - 1].frame == frame) {
			this.count--;
		}
		if (this.count < MAX_SPANS) {
			this.spans[this.count++].set(frame, playing, tempo, numerator, denominator, quarters, samples);
		}
	}

	// the clock's owner waits for each block, however long it takes (an offline render, an export); a live clock cannot
	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	public boolean isOffline() {
		return this.offline;
	}

	public int count() {
		return this.count;
	}

	public Span get(int index) {
		return this.spans[index];
	}

	public static final class Span {
		private int frame;
		private boolean playing;
		private double tempo;
		private int numerator;
		private int denominator;
		private double quarters;
		private long samples;

		private void set(int frame, boolean playing, double tempo, int numerator, int denominator, double quarters, long samples) {
			this.frame = frame;
			this.playing = playing;
			this.tempo = tempo;
			this.numerator = numerator;
			this.denominator = denominator;
			this.quarters = quarters;
			this.samples = samples;
		}

		// the first frame of the block it holds
		public int getFrame() {
			return this.frame;
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

		// at its first frame
		public double getQuarters() {
			return this.quarters;
		}

		// at its first frame
		public long getSamples() {
			return this.samples;
		}
	}
}
