package app.tuxguitar.midi.synth;

/**
 * Delays the blocks of a stream by a number of frames: each block comes out as the stream was that many frames earlier,
 * silence before the stream began. When the delay grows, silence fills the gap; when it shrinks, the oldest frames held
 * are dropped. It runs on the audio thread, so it allocates only when the delay grows past what it can hold.
 */
public final class TGDelayLine {

	private static final int FRAME_BYTES = TGAudioBuffer.CHANNELS * 2;

	// the frames that came in and have not gone out yet, oldest first from head, wrapping around the ring
	private byte[] ring = new byte[0];
	private int head;
	private int held;

	public void delay(TGAudioBuffer buffer, int frames) {
		byte[] block = buffer.getBuffer();
		this.hold(frames * FRAME_BYTES, block.length);
		this.write(block, this.head + this.held);
		this.read(block, this.head);
		this.head = (this.head + block.length) % this.ring.length;
	}

	// as many bytes held as the delay: silence before the oldest when it grew, the oldest dropped when it shrank; and
	// room for a block after them
	private void hold(int delay, int block) {
		if( this.ring.length < delay + block ) {
			this.grow(delay + block);
		}
		int silence = delay - this.held;
		this.head = Math.floorMod(this.head - silence, this.ring.length);
		for( int i = 0 ; i < silence ; i ++ ) {
			this.ring[(this.head + i) % this.ring.length] = 0;
		}
		this.held = delay;
	}

	// a larger ring holding the same bytes, oldest at its start
	private void grow(int size) {
		byte[] grown = new byte[size];
		for( int i = 0 ; i < this.held ; i ++ ) {
			grown[i] = this.ring[(this.head + i) % this.ring.length];
		}
		this.ring = grown;
		this.head = 0;
	}

	// the block into the ring from the position, wrapping around
	private void write(byte[] block, int position) {
		int start = position % this.ring.length;
		int first = Math.min(block.length, this.ring.length - start);
		System.arraycopy(block, 0, this.ring, start, first);
		System.arraycopy(block, first, this.ring, 0, block.length - first);
	}

	// the ring from the position, wrapping around, into the block
	private void read(byte[] block, int position) {
		int first = Math.min(block.length, this.ring.length - position);
		System.arraycopy(this.ring, position, block, 0, first);
		System.arraycopy(this.ring, 0, block, first, block.length - first);
	}
}
