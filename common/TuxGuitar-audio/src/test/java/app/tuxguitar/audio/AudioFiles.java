package app.tuxguitar.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * An audio file read back as any player reads it (Java Sound's file readers): its type, its format and its samples,
 * left and right, as floats (integers scaled so full scale is 1).
 */
public final class AudioFiles {

	private final String type;
	private final AudioFormat format;
	private final float[][] samples;

	private AudioFiles(String type, AudioFormat format, float[][] samples) {
		this.type = type;
		this.format = format;
		this.samples = samples;
	}

	public static AudioFiles read(byte[] file) throws IOException, UnsupportedAudioFileException {
		String type = AudioSystem.getAudioFileFormat(new ByteArrayInputStream(file)).getType().toString();
		AudioInputStream stream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(file));
		AudioFormat format = stream.getFormat();
		ByteBuffer data = ByteBuffer.wrap(stream.readAllBytes()).order(format.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
		int frames = data.remaining() / format.getFrameSize();
		float[][] samples = new float[format.getChannels()][frames];
		for (int frame = 0; frame < frames; frame++) {
			for (int side = 0; side < samples.length; side++) {
				samples[side][frame] = sample(data, format);
			}
		}
		return new AudioFiles(type, format, samples);
	}

	private static float sample(ByteBuffer data, AudioFormat format) {
		if (AudioFormat.Encoding.PCM_FLOAT.equals(format.getEncoding())) {
			return data.getFloat();
		}
		if (format.getSampleSizeInBits() == 16) {
			return data.getShort() / 32767f;
		}
		int value = 0;
		for (int i = 0; i < 3; i++) {
			int shift = (data.order() == ByteOrder.BIG_ENDIAN ? 16 - 8 * i : 8 * i);
			value |= (data.get() & 0xff) << shift;
		}
		return ((value << 8) >> 8) / 8388607f;
	}

	// "WAVE 44100 Hz 16 bits PCM_SIGNED", as a scenario names it
	public String describe() {
		return this.type + " " + (int) this.format.getSampleRate() + " Hz " + this.format.getSampleSizeInBits() + " bits " + this.format.getEncoding();
	}

	public int getSampleRate() {
		return (int) this.format.getSampleRate();
	}

	public float[][] getSamples() {
		return this.samples;
	}
}
