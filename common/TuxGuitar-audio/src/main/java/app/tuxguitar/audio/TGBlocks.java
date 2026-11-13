package app.tuxguitar.audio;

import app.tuxguitar.midi.synth.TGAudioBuffer;

/**
 * Taps rendered a block of 512 samples at a time from a sound chain that sounds what it is sent its latency later
 * (Gervill's two control periods, plus its effects'). Offline that latency is taken out: the blocks run on past the end
 * of the timeline by the latency, and each lands in the tap the latency earlier than it was rendered.
 */
final class TGBlocks {

	static final int FRAMES = TGAudioBuffer.BUFFER_SIZE / 2;

	private TGBlocks() {
	}

	// the samples of a side from start on, into the start of the block; past the side's end, nothing
	static void take(float[] side, long start, float[] block) {
		int count = (int) Math.min(block.length, side.length - start);
		if (count > 0) {
			System.arraycopy(side, (int) start, block, 0, count);
		}
	}

	// the block whose first sample the chain sounds at start (in samples of the tap, after the latency), into the tap;
	// what falls outside the tap is dropped
	static void place(float[][] block, float[][] tap, long start) {
		int from = (int) Math.max(0, -start);
		for (int side = 0; side < tap.length; side++) {
			int to = (int) Math.min(FRAMES, tap[side].length - start);
			if (from < to) {
				System.arraycopy(block[side], from, tap[side], (int) (start + from), to - from);
			}
		}
	}
}
