package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import app.tuxguitar.audio.features.AudioWorld;
import app.tuxguitar.song.models.TGTake;

/**
 * A take in a TuxGuitar file is an uncompressed 32-bit float WAVE entry at the sample rate it was recorded at, which
 * any player reads; read back, it is sample for sample what it was. Takes may be long: they stream through a small
 * buffer, so writing one does not hold its samples twice, and reading one holds them once.
 */
public class TestTakeWave {

	private static final float[][] STEREO = {{0.5f, -0.25f, 1.5f, 0f}, {-0.5f, 0.125f, -2f, 1f}};

	private final TGTakeWave wave = new TGTakeWave();

	private byte[] written(TGTake take) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		this.wave.write(take, out);
		return out.toByteArray();
	}

	private static float[][] samples(TGTake take) {
		float[][] samples = new float[take.countChannels()][];
		for (int channel = 0; channel < samples.length; channel++) {
			samples[channel] = AudioWorld.samples(take, channel);
		}
		return samples;
	}

	private static long allocated() {
		return ((com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean()).getThreadAllocatedBytes(Thread.currentThread().getId());
	}

	// its header also says how many bytes a second and a frame take: 48000 frames of 2 floats
	@Test
	public void aTakeIsAFloatWaveFileAnyPlayerReadsAtItsSampleRate() throws Exception {
		byte[] written = written(new TGTake(3, STEREO, 48000));
		AudioFiles file = AudioFiles.read(written);
		ByteBuffer header = ByteBuffer.wrap(written).order(ByteOrder.LITTLE_ENDIAN);

		assertEquals("WAVE 48000 Hz 32 bits PCM_FLOAT wav | 384000 8", file.describe() + " " + this.wave.getExtension() + " | " + header.getInt(28) + " " + header.getShort(32));
		assertArrayEquals(STEREO[0], file.getSamples()[0], 0f);
		assertArrayEquals(STEREO[1], file.getSamples()[1], 0f);
	}

	// numbered by the file it is in, not by its entry
	@Test
	public void monoAndStereoTakesComeBackSampleForSampleAtTheirRates() throws Exception {
		TGTake mono = this.wave.read(new ByteArrayInputStream(written(new TGTake(1, new float[] {0.25f, -1.75f, 3e-7f}, 44100))));
		TGTake stereo = this.wave.read(new ByteArrayInputStream(written(new TGTake(2, STEREO, 96000))));

		assertEquals("0 44100 1 | 0 96000 2", mono.getNumber() + " " + mono.getSampleRate() + " " + mono.countChannels() + " | " + stereo.getNumber() + " " + stereo.getSampleRate() + " " + stereo.countChannels());
		assertArrayEquals(new float[][] {{0.25f, -1.75f, 3e-7f}}, samples(mono));
		assertArrayEquals(STEREO, samples(stereo));
	}

	// a wave file of chunks a reader skips (a list of tags) and an odd-sized one, padded: the samples are still read
	@Test
	public void chunksOtherThanTheFormatAndTheSamplesAreSkipped() throws Exception {
		byte[] file = written(new TGTake(1, STEREO, 44100));
		byte[] extra = {'L', 'I', 'S', 'T', 3, 0, 0, 0, 'a', 'b', 'c', 0};
		ByteArrayOutputStream tagged = new ByteArrayOutputStream();
		tagged.write(file, 0, 36);
		tagged.write(extra);
		tagged.write(file, 36, file.length - 36);

		assertArrayEquals(STEREO, samples(this.wave.read(new ByteArrayInputStream(tagged.toByteArray()))));
	}

	// a minute of stereo at 48000 Hz is 23 MB of samples
	@Test
	public void writingALongTakeDoesNotHoldItsSamplesTwice() throws Exception {
		TGTake take = new TGTake(1, new float[2][48000 * 60], 48000);
		OutputStream counted = new OutputStream() {
			long bytes;

			public void write(int b) {
				this.bytes++;
			}

			public void write(byte[] b, int off, int len) {
				this.bytes += len;
			}

			public String toString() {
				return String.valueOf(this.bytes);
			}
		};
		long before = allocated();
		this.wave.write(take, counted);
		long allocated = allocated() - before;

		assertTrue(allocated < 256 * 1024 && counted.toString().equals(String.valueOf(44 + 2 * 4 * 48000 * 60)), allocated + " bytes allocated, " + counted + " written");
	}

	@Test
	public void readingALongTakeHoldsItsSamplesOnce() throws Exception {
		byte[] file = written(new TGTake(1, new float[2][48000 * 60], 48000));
		long before = allocated();
		TGTake take = this.wave.read(new ByteArrayInputStream(file));
		long allocated = allocated() - before;
		long samples = 2L * 4 * 48000 * 60;

		assertTrue(take.countSamples() == 48000 * 60 && allocated < samples + 256 * 1024, allocated + " bytes allocated for " + samples + " bytes of samples");
	}

	// only what TuxGuitar writes: 32-bit float wave files
	@Test
	public void aWaveFileOfIntegersIsRefused() throws Exception {
		ByteArrayOutputStream file = new ByteArrayOutputStream();
		TGAudioFileWriter.write(STEREO, new TGExportFormat(TGExportFormat.Type.WAVE, 44100, TGExportFormat.Samples.PCM_16), file);

		assertThrows(IOException.class, () -> this.wave.read(new ByteArrayInputStream(file.toByteArray())));
	}

	// a take's file but for one field: not a RIFF file, not a WAVE file, integers of 32 bits, floats of 64 bits
	@Test
	public void aFileThatIsNoWaveFileOfFloatsIsRefused() throws Exception {
		byte[] file = written(new TGTake(1, STEREO, 44100));

		assertRefused(patched(file, 0, "RIFX"));
		assertRefused(patched(file, 8, "WAVX"));
		assertRefused(patched(file, 20, "\u0001\u0000"));
		assertRefused(patched(file, 34, "\u0040\u0000"));
	}

	private void assertRefused(byte[] file) {
		assertThrows(IOException.class, () -> this.wave.read(new ByteArrayInputStream(file)));
	}

	// the file with the bytes from the offset replaced by the text's
	private static byte[] patched(byte[] file, int offset, String bytes) {
		byte[] patched = file.clone();
		System.arraycopy(bytes.getBytes(StandardCharsets.ISO_8859_1), 0, patched, offset, bytes.length());
		return patched;
	}
}
