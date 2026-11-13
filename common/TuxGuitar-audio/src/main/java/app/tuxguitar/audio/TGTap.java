package app.tuxguitar.audio;

/**
 * A channel's tap, left and right, rendered offline with its chain's latency taken out, and that latency: how many
 * samples later than its events the chain sounds them live (as it was when the render ended).
 */
public final class TGTap {

	private final float[][] samples;
	private final int latency;

	public TGTap(float[][] samples, int latency) {
		this.samples = samples;
		this.latency = latency;
	}

	public float[][] getSamples() {
		return this.samples;
	}

	public int getLatency() {
		return this.latency;
	}
}
