package app.tuxguitar.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

/**
 * What an exported file is: its type, its sample rate and how each sample is written, left and right. A WAVE file holds
 * 16 or 24-bit integers (little-endian) or 32-bit floats; an AIFF file holds integers (big-endian). Immutable.
 */
public final class TGExportFormat {

	public enum Type {
		WAVE(AudioFileFormat.Type.WAVE, false),
		AIFF(AudioFileFormat.Type.AIFF, true);

		private final AudioFileFormat.Type fileType;
		private final boolean bigEndian;

		Type(AudioFileFormat.Type fileType, boolean bigEndian) {
			this.fileType = fileType;
			this.bigEndian = bigEndian;
		}
	}

	// how a sample is written: integers are clipped to full scale, a float keeps what goes above it
	public enum Samples {
		PCM_16(2),
		PCM_24(3),
		FLOAT_32(4);

		private final int bytes;

		Samples(int bytes) {
			this.bytes = bytes;
		}
	}

	private final Type type;
	private final int sampleRate;
	private final Samples samples;

	// an AIFF file of floats is refused
	public TGExportFormat(Type type, int sampleRate, Samples samples) {
		if (type == Type.AIFF && samples == Samples.FLOAT_32) {
			throw new IllegalArgumentException("an AIFF file holds integer samples only");
		}
		this.type = type;
		this.sampleRate = sampleRate;
		this.samples = samples;
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
