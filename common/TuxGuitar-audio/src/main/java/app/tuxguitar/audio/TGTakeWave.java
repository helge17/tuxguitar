package app.tuxguitar.audio;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import app.tuxguitar.io.tg.TGTakeCodec;
import app.tuxguitar.song.models.TGTake;

/**
 * A take in a TuxGuitar file (format 3.0): an uncompressed WAVE file of 32-bit float samples (IEEE float, little
 * endian, the channels interleaved) at the sample rate the take was recorded at; it is resampled only to play or export.
 * The samples stream through a buffer of BLOCK frames both ways: writing does not copy a take, reading fills the take's
 * own arrays. Reading skips the chunks it does not need, and refuses what is not a 32-bit float wave file.
 */
public final class TGTakeWave implements TGTakeCodec {

	private static final int BLOCK = 4096;
	private static final int BYTES = 4;
	private static final short IEEE_FLOAT = 3;

	public String getExtension() {
		return "wav";
	}

	public void write(TGTake take, OutputStream out) throws IOException {
		int channels = take.countChannels();
		int data = take.countSamples() * channels * BYTES;
		ByteBuffer header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN);
		header.put(ascii("RIFF")).putInt(36 + data).put(ascii("WAVE"));
		header.put(ascii("fmt ")).putInt(16).putShort(IEEE_FLOAT).putShort((short) channels).putInt(take.getSampleRate());
		header.putInt(take.getSampleRate() * channels * BYTES).putShort((short) (channels * BYTES)).putShort((short) (BYTES * 8));
		header.put(ascii("data")).putInt(data);
		out.write(header.array());
		ByteBuffer block = ByteBuffer.allocate(BLOCK * channels * BYTES).order(ByteOrder.LITTLE_ENDIAN);
		for (int frame = 0; frame < take.countSamples(); frame += BLOCK) {
			block.clear();
			put(take, frame, Math.min(frame + BLOCK, take.countSamples()), block);
			out.write(block.array(), 0, block.position());
		}
	}

	// the frames from one to another, interleaved
	private static void put(TGTake take, int from, int to, ByteBuffer block) {
		for (int frame = from; frame < to; frame++) {
			for (int channel = 0; channel < take.countChannels(); channel++) {
				block.putFloat(take.getSample(channel, frame));
			}
		}
	}

	public TGTake read(InputStream in) throws IOException {
		ByteBuffer riff = bytes(in, 12);
		if (!"RIFF".equals(ascii(riff, 0)) || !"WAVE".equals(ascii(riff, 8))) {
			throw new IOException("Not a wave file");
		}
		ByteBuffer format = chunk(in, "fmt ");
		if (format.getShort(0) != IEEE_FLOAT || format.getShort(14) != BYTES * 8) {
			throw new IOException("Not a wave file of 32-bit float samples");
		}
		float[][] samples = new float[format.getShort(2)][chunkSize(in, "data") / (format.getShort(2) * BYTES)];
		read(in, samples);
		return TGTake.adopt(0, samples, format.getInt(4));
	}

	// the frames, interleaved, into each channel's samples, through one block
	private static void read(InputStream in, float[][] samples) throws IOException {
		int frames = samples[0].length;
		ByteBuffer block = ByteBuffer.allocate(BLOCK * samples.length * BYTES).order(ByteOrder.LITTLE_ENDIAN);
		for (int frame = 0; frame < frames; frame += BLOCK) {
			int to = Math.min(frame + BLOCK, frames);
			fill(in, block, (to - frame) * samples.length * BYTES);
			for (int i = frame; i < to; i++) {
				get(block, samples, i);
			}
		}
	}

	private static void fill(InputStream in, ByteBuffer block, int count) throws IOException {
		block.clear();
		if (in.readNBytes(block.array(), 0, count) < count) {
			throw new EOFException("The wave file ends early");
		}
	}

	private static void get(ByteBuffer block, float[][] samples, int frame) {
		for (float[] channel : samples) {
			channel[frame] = block.getFloat();
		}
	}

	// the body of the next chunk of the id, the chunks before it skipped
	private static ByteBuffer chunk(InputStream in, String id) throws IOException {
		return bytes(in, chunkSize(in, id));
	}

	// the size of the next chunk of the id, whose body is next in the stream; the chunks before it skipped (each is
	// padded to an even size)
	private static int chunkSize(InputStream in, String id) throws IOException {
		ByteBuffer header = bytes(in, 8);
		while (!id.equals(ascii(header, 0))) {
			int size = header.getInt(4);
			bytes(in, size + (size & 1));
			header = bytes(in, 8);
		}
		return header.getInt(4);
	}

	private static ByteBuffer bytes(InputStream in, int count) throws IOException {
		ByteBuffer bytes = ByteBuffer.allocate(count).order(ByteOrder.LITTLE_ENDIAN);
		fill(in, bytes, count);
		return bytes;
	}

	private static byte[] ascii(String text) {
		return text.getBytes(StandardCharsets.US_ASCII);
	}

	private static String ascii(ByteBuffer buffer, int at) {
		return new String(buffer.array(), at, 4, StandardCharsets.US_ASCII);
	}
}
