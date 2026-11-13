package app.tuxguitar.midi.synth;

/**
 * Delays the blocks of a stream by a number of frames: each block comes out as the stream was that many frames earlier,
 * silence before the stream began. When the delay grows, silence fills the gap; when it shrinks, the oldest frames held
 * are dropped. The blocks are 32-bit float, one array per side. It runs on the audio thread, so it allocates only when
 * the delay grows past what it can hold.
 */
public final class TGDelayLine {

	// per side, the frames that came in and have not gone out yet, oldest first from head, wrapping around the ring
	private float[][] ring = new float[TGAudioBuffer.CHANNELS][0];
	private int head;
	private int held;

	public void delay(float[][] block, int frames) {
		int length = block[0].length;
		this.hold(frames, length);
		for( int side = 0 ; side < block.length ; side ++ ) {
			write(block[side], this.ring[side], this.head + this.held);
			read(block[side], this.ring[side], this.head);
		}
		this.head = (this.head + length) % this.ring[0].length;
	}

	// as many frames held as the delay: silence before the oldest when it grew, the oldest dropped when it shrank; and
	// room for a block after them
	private void hold(int delay, int block) {
		if( this.ring[0].length < delay + block ) {
			this.grow(delay + block);
		}
		int silence = delay - this.held;
		this.head = Math.floorMod(this.head - silence, this.ring[0].length);
		for( float[] side : this.ring ) {
			for( int i = 0 ; i < silence ; i ++ ) {
				side[(this.head + i) % side.length] = 0f;
			}
		}
		this.held = delay;
	}

	// a larger ring holding the same frames, oldest at its start
	private void grow(int size) {
		float[][] grown = new float[this.ring.length][size];
		for( int side = 0 ; side < this.ring.length ; side ++ ) {
			for( int i = 0 ; i < this.held ; i ++ ) {
				grown[side][i] = this.ring[side][(this.head + i) % this.ring[side].length];
			}
		}
		this.ring = grown;
		this.head = 0;
	}

	// the side's block into its ring from the position, wrapping around
	private static void write(float[] block, float[] ring, int position) {
		int start = position % ring.length;
		int first = Math.min(block.length, ring.length - start);
		System.arraycopy(block, 0, ring, start, first);
		System.arraycopy(block, first, ring, 0, block.length - first);
	}

	// the side's ring from the position, wrapping around, into its block
	private static void read(float[] block, float[] ring, int position) {
		int first = Math.min(block.length, ring.length - position);
		System.arraycopy(ring, position, block, 0, first);
		System.arraycopy(ring, 0, block, first, block.length - first);
	}
}
