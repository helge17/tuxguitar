package app.tuxguitar.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

/**
 * What an exported file is: its type, its sample rate and how each sample is written, left and right. A WAVE file holds
 * 16 or 24-bit integers (little-endian) or 32-bit floats; an AIFF file holds integers (big-endian); an AU file 16-bit
 * integers (big-endian). Immutable. An app offers the formats of offered(), each named "<type> <samples>".
 */
public final class TGExportFormat {

	// the rate an app's export writes at (the TuxGuitar Synthesizer's)
	private static final int OFFERED_RATE = 44100;

	// how a sample is written: integers are clipped to full scale, a float keeps what goes above it
	public enum Samples {
		PCM_16(2, "16-bit"),
		PCM_24(3, "24-bit"),
		FLOAT_32(4, "32-bit float");

		private final int bytes;
		private final String name;

		Samples(int bytes, String name) {
			this.bytes = bytes;
			this.name = name;
		}
	}

	public enum Type {
		WAVE(AudioFileFormat.Type.WAVE, false, EnumSet.allOf(Samples.class)),
		AIFF(AudioFileFormat.Type.AIFF, true, EnumSet.of(Samples.PCM_16, Samples.PCM_24)),
		AU(AudioFileFormat.Type.AU, true, EnumSet.of(Samples.PCM_16));

		private final AudioFileFormat.Type fileType;
		private final boolean bigEndian;
		// the samples a file of the type holds
		private final Set<Samples> holds;

		Type(AudioFileFormat.Type fileType, boolean bigEndian, Set<Samples> holds) {
			this.fileType = fileType;
			this.bigEndian = bigEndian;
			this.holds = holds;
		}
	}

	private final Type type;
	private final int sampleRate;
	private final Samples samples;

	// samples the type does not hold are refused (an AIFF file of floats, an AU file of other than 16-bit integers)
	public TGExportFormat(Type type, int sampleRate, Samples samples) {
		if (!type.holds.contains(samples)) {
			throw new IllegalArgumentException("an " + type + " file holds no " + samples.name + " samples");
		}
		this.type = type;
		this.sampleRate = sampleRate;
		this.samples = samples;
	}

	// what an app's export offers, the first being its default
	public static List<TGExportFormat> offered() {
		return Arrays.asList(
			new TGExportFormat(Type.WAVE, OFFERED_RATE, Samples.PCM_16),
			new TGExportFormat(Type.WAVE, OFFERED_RATE, Samples.PCM_24),
			new TGExportFormat(Type.WAVE, OFFERED_RATE, Samples.FLOAT_32),
			new TGExportFormat(Type.AIFF, OFFERED_RATE, Samples.PCM_16),
			new TGExportFormat(Type.AU, OFFERED_RATE, Samples.PCM_16));
	}

	// the format offered with the name; refused when none is
	public static TGExportFormat named(String name) {
		return offered().stream().filter(format -> format.getName().equals(name)).findFirst().orElseThrow(() -> new IllegalArgumentException("no export format is named " + name));
	}

	// "WAVE 16-bit"
	public String getName() {
		return this.type + " " + this.samples.name;
	}

	// of its files, without the dot
	public String getExtension() {
		return this.type.fileType.getExtension();
	}

	public Type getType() {
		return this.type;
	}

	public int getSampleRate() {
		return this.sampleRate;
	}

	public Samples getSamples() {
		return this.samples;
	}

	AudioFileFormat.Type fileType() {
		return this.type.fileType;
	}

	AudioFormat audioFormat() {
		AudioFormat.Encoding encoding = (this.samples == Samples.FLOAT_32 ? AudioFormat.Encoding.PCM_FLOAT : AudioFormat.Encoding.PCM_SIGNED);
		return new AudioFormat(encoding, this.sampleRate, 8 * this.samples.bytes, 2, 2 * this.samples.bytes, this.sampleRate, this.type.bigEndian);
	}

	// the frames, left then right, as the file holds them
	byte[] encode(float[][] frames) {
		ByteBuffer buffer = ByteBuffer.allocate(frames[0].length * 2 * this.samples.bytes);
		buffer.order(this.type.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
		for (int frame = 0; frame < frames[0].length; frame++) {
			this.put(buffer, frames[0][frame]);
			this.put(buffer, frames[1][frame]);
		}
		return buffer.array();
	}

	// an integer's bytes in the file's order
	private void put(ByteBuffer buffer, float sample) {
		if (this.samples == Samples.FLOAT_32) {
			buffer.putFloat(sample);
			return;
		}
		int bytes = this.samples.bytes;
		int value = Math.round(Math.max(-1f, Math.min(1f, sample)) * ((1 << (8 * bytes - 1)) - 1));
		for (int i = 0; i < bytes; i++) {
			buffer.put((byte) (value >> (8 * (this.type.bigEndian ? bytes - 1 - i : i))));
		}
	}
}
