package app.tuxguitar.song.models;

/**
 * One recording on an audio track: its number, given in recording order and never changed, and its samples, as the
 * audio device delivered them (32-bit float, at the device's sample rate): one channel for a mono take, two (left,
 * then right) for a stereo one, all of the same length. A take made by pasting has no samples of its own: its clips
 * play the samples of the takes that played where it was copied. Immutable: the samples never change once recorded, so
 * a cloned track, a pasted measure or an undo snapshot shares the take instead of copying it.
 */
public final class TGTake {

	private final int number;
	private final float[][] samples;
	private final int sampleRate;

	// a mono take
	public TGTake(int number, float[] samples, int sampleRate) {
		this(number, new float[][] {samples}, sampleRate);
	}

	// the samples are copied once, here, so no one keeps a way to change them
	public TGTake(int number, float[][] samples, int sampleRate) {
		this(sampleRate, number, copy(samples));
	}

	// the samples become the take's
	private TGTake(int sampleRate, int number, float[][] samples) {
		this.number = number;
		this.samples = samples;
		this.sampleRate = sampleRate;
	}

	private static float[][] copy(float[][] samples) {
		float[][] copy = new float[samples.length][];
		for (int channel = 0; channel < samples.length; channel++) {
			copy[channel] = samples[channel].clone();
		}
		return copy;
	}

	// a take of the samples as they are, not copied: for one who made them and keeps no other way to change them (a
	// file reader), so a long take is not held twice
	public static TGTake adopt(int number, float[][] samples, int sampleRate) {
		return new TGTake(sampleRate, number, samples);
	}

	// the same samples under another number: shared, as they never change
	public TGTake renumbered(int number) {
		return new TGTake(this.sampleRate, number, this.samples);
	}

	// the audio device's sample rate it was recorded at
	public int getSampleRate() {
		return this.sampleRate;
	}

	public int getNumber() {
		return this.number;
	}

	public int countChannels() {
		return this.samples.length;
	}

	public int countSamples() {
		return this.samples[0].length;
	}

	public float getSample(int channel, int index) {
		return this.samples[channel][index];
	}
}
