package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioSystem;

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

	// Sun/NeXT AU: big-endian 16-bit integers only
	@Test
	public void anAuFileHoldsBigEndian16BitIntegersAndRefusesOthers() throws Exception {
		long frames = assertWritten("AU 44100 Hz 16 bits PCM_SIGNED", Type.AU, 44100, Samples.PCM_16, CLIPPED, 1 / 32767f);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TGAudioFileWriter.write(SAMPLES, new TGExportFormat(Type.AU, 44100, Samples.PCM_16), out);
		boolean bigEndian = AudioSystem.getAudioFileFormat(new ByteArrayInputStream(out.toByteArray())).getFormat().isBigEndian();
		assertEquals("4 true", frames + " " + bigEndian);
		assertThrows(IllegalArgumentException.class, () -> new TGExportFormat(Type.AU, 44100, Samples.PCM_24));
		assertThrows(IllegalArgumentException.class, () -> new TGExportFormat(Type.AU, 44100, Samples.FLOAT_32));
	}

	// what an app's export offers, at 44100 Hz, by name, with the extension of its files (Java Sound's: .aif, as the
	// desktop's export names AIFF files today)
	@Test
	public void theFormatsOffered() {
		String offered = TGExportFormat.offered().stream().map(format -> format.getName() + " ." + format.getExtension() + " " + format.getSampleRate()).collect(Collectors.joining(" | "));
		assertEquals("WAVE 16-bit .wav 44100 | WAVE 24-bit .wav 44100 | WAVE 32-bit float .wav 44100 | AIFF 16-bit .aif 44100 | AU 16-bit .au 44100", offered);
	}

	@Test
	public void anAiffFileOfFloatsIsRefused() {
		assertThrows(IllegalArgumentException.class, () -> new TGExportFormat(Type.AIFF, 44100, Samples.FLOAT_32));
	}
}
