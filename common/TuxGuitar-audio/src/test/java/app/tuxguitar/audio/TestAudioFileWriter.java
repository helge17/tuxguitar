package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;

import app.tuxguitar.audio.TGExportFormat.Samples;
import app.tuxguitar.audio.TGExportFormat.Type;

/**
 * Exported samples written as a file of each format, then read back as any player reads it. Integer samples are clipped
 * to full scale; a float keeps what goes above it.
 */
public class TestAudioFileWriter {

	private static final float[][] SAMPLES = {{0.5f, -0.25f, 1.5f, 0f}, {-0.5f, 0.125f, -2f, 1f}};
	private static final float[][] CLIPPED = {{0.5f, -0.25f, 1f, 0f}, {-0.5f, 0.125f, -1f, 1f}};

	// the file read back is of the format, and holds the samples within the step of its integers
	private static long assertWritten(String format, Type type, int sampleRate, Samples samples, float[][] expected, float within) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TGAudioFileWriter.write(SAMPLES, new TGExportFormat(type, sampleRate, samples), out);
		AudioFiles file = AudioFiles.read(out.toByteArray());
		assertEquals(format, file.describe());
		assertArrayEquals(expected[0], file.getSamples()[0], within, format);
		assertArrayEquals(expected[1], file.getSamples()[1], within, format);
		return file.getFrameLength();
	}

	@Test
	public void integerSamplesAreClippedToFullScaleInEachFileType() throws Exception {
		long[] frames = {
			assertWritten("WAVE 44100 Hz 16 bits PCM_SIGNED", Type.WAVE, 44100, Samples.PCM_16, CLIPPED, 1 / 32767f),
			assertWritten("WAVE 48000 Hz 24 bits PCM_SIGNED", Type.WAVE, 48000, Samples.PCM_24, CLIPPED, 1 / 8388607f),
			assertWritten("AIFF 44100 Hz 16 bits PCM_SIGNED", Type.AIFF, 44100, Samples.PCM_16, CLIPPED, 1 / 32767f),
			assertWritten("AIFF 22050 Hz 24 bits PCM_SIGNED", Type.AIFF, 22050, Samples.PCM_24, CLIPPED, 1 / 8388607f)};
		// each header says how many frames the file holds
		assertArrayEquals(new long[] {4, 4, 4, 4}, frames);
	}

	@Test
	public void aWaveFileOfFloatSamplesKeepsWhatGoesAboveFullScale() throws Exception {
		assertWritten("WAVE 44100 Hz 32 bits PCM_FLOAT", Type.WAVE, 44100, Samples.FLOAT_32, SAMPLES, 0f);
	}

	@Test
	public void anAiffFileOfFloatsIsRefused() {
		assertThrows(IllegalArgumentException.class, () -> new TGExportFormat(Type.AIFF, 44100, Samples.FLOAT_32));
	}
}
