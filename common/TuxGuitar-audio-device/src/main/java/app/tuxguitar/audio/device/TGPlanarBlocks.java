package app.tuxguitar.audio.device;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * The samples of a stream's blocks as they cross to and from the native side: a direct buffer (native order, 32-bit
 * float, planar: channel c from c * block) and one array per channel, reused from block to block, so a block allocates
 * nothing.
 */
final class TGPlanarBlocks {

	private final int block;
	private final ByteBuffer buffer;
	private final FloatBuffer floats;
	private final float[][] channels;

	TGPlanarBlocks(int channels, int block) {
		this.block = block;
		this.buffer = ByteBuffer.allocateDirect(Math.max(1, channels) * block * Float.BYTES).order(ByteOrder.nativeOrder());
		this.floats = this.buffer.asFloatBuffer();
		this.channels = new float[channels][block];
	}

	ByteBuffer getBuffer() {
		return this.buffer;
	}

	float[][] getChannels() {
		return this.channels;
	}

	// the buffer's frames into the arrays
	void read(int frames) {
		for (int c = 0; c < this.channels.length; c++) {
			this.floats.position(c * this.block);
			this.floats.get(this.channels[c], 0, frames);
		}
	}

	// the arrays' frames into the buffer
	void write(int frames) {
		for (int c = 0; c < this.channels.length; c++) {
			this.floats.position(c * this.block);
			this.floats.put(this.channels[c], 0, frames);
		}
	}
}
