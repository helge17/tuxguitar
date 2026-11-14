package app.tuxguitar.vst3;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * The audio of one plugin instance, shared with the host process: a file both map, holding the input block's left and
 * right sides, then the output block's, each as long as the maximum block, 32-bit float in the machine's byte order
 * (native/host/shared.h is the other side).
 */
final class TGVst3SharedBlocks {

	private final File file;
	private final int maxBlock;
	private final FloatBuffer floats;

	private TGVst3SharedBlocks(File file, int maxBlock, FloatBuffer floats) {
		this.file = file;
		this.maxBlock = maxBlock;
		this.floats = floats;
	}

	static TGVst3SharedBlocks create(File folder, int maxBlock) throws TGVst3Exception {
		try {
			File file = File.createTempFile("blocks-", ".bin", folder);
			file.deleteOnExit();
			try (RandomAccessFile access = new RandomAccessFile(file, "rw")) {
				long size = 4L * maxBlock * Float.BYTES;
				access.setLength(size);
				MappedByteBuffer mapped = access.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, size);
				mapped.order(ByteOrder.nativeOrder());
				return new TGVst3SharedBlocks(file, maxBlock, mapped.asFloatBuffer());
			}
		} catch (IOException e) {
			throw new TGVst3Exception("cannot share audio with the VST3 host in " + folder, e);
		}
	}

	String getPath() {
		return this.file.getAbsolutePath();
	}

	// the first frames of each side of the block, as the plugin's input
	void write(float[][] block, int frames) {
		for (int side = 0; side < 2; side++) {
			this.floats.position(side * this.maxBlock);
			this.floats.put(block[side], 0, frames);
		}
	}

	// the plugin's output, into the first frames of each side of the block
	void read(float[][] block, int frames) {
		for (int side = 0; side < 2; side++) {
			this.floats.position((2 + side) * this.maxBlock);
			this.floats.get(block[side], 0, frames);
		}
	}

	// the file goes when the host process lets go of it (at the latest when TuxGuitar exits)
	void close() {
		this.file.delete();
	}
}
