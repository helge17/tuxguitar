package app.tuxguitar.audio;

import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.midi.synth.impl.GervillProcessor;

/**
 * Taps rendered from Gervill a block of 512 samples at a time. Gervill's stream sounds an event its latency after
 * Gervill renders it, so a tap of the timeline is the stream read that much later: the blocks run on past the end of
 * the timeline by the latency, and each lands in the tap the latency earlier than it was rendered.
 */
final class TGGervillBlocks {

	static final int FRAMES = TGAudioBuffer.BUFFER_SIZE / 2;

	private TGGervillBlocks() {
	}

	static int latency(TGTimeline timeline) {
		return GervillProcessor.latency(timeline.getSampleRate());
	}

	// the first samples of the blocks that make a tap of the timeline
	static long end(TGTimeline timeline) {
		return timeline.length() + latency(timeline);
	}

	// the block whose first sample the stream sounds at start (in samples of the tap, after the latency), into the tap;
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
