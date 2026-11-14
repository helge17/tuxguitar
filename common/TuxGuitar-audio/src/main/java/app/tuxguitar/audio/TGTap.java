package app.tuxguitar.audio;

/**
 * A channel's tap, left and right, rendered offline with its chain's latency taken out, and that latency: how many
 * samples later than its events the chain sounds them live (as it was when the render ended).
 */
public final class TGTap {

	private final float[][] samples;
	private final int latency;
	private final int tail;

	// tail: how many samples the chain sounds on after its input or its last note (TGAudioProcessor.INFINITE_TAIL: for
	// ever)
	public TGTap(float[][] samples, int latency, int tail) {
		this.samples = samples;
		this.latency = latency;
		this.tail = tail;
	}

	public float[][] getSamples() {
		return this.samples;
	}

	public int getLatency() {
		return this.latency;
	}

	public int getTail() {
		return this.tail;
	}
}
