package app.tuxguitar.song.models;

/**
 * One recording on an audio track: its number, given in recording order and never changed, and its samples, as the
 * audio device delivered them (32-bit float, mono, at the device's sample rate). A take made by pasting has no samples
 * of its own: its clips play the samples of the takes that played where it was copied. Immutable: the samples never change once recorded, so a cloned
 * track, a pasted measure or an undo snapshot shares the take instead of copying it.
 */
public final class TGTake {

	private final int number;
	private final float[] samples;
	private final int sampleRate;

	// the samples are copied once, here, so no one keeps a way to change them
	public TGTake(int number, float[] samples, int sampleRate) {
		this.number = number;
		this.samples = samples.clone();
		this.sampleRate = sampleRate;
	}

	// the audio device's sample rate it was recorded at
	public int getSampleRate() {
		return this.sampleRate;
	}

	public int getNumber() {
		return this.number;
	}

	public int countSamples() {
		return this.samples.length;
	}

	public float getSample(int index) {
		return this.samples[index];
	}
}
