package app.tuxguitar.midi.synth;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Test;

/**
 * A delay line over blocks of 512 frames. The stream is a ramp, a different level on each frame of each side, so a
 * shift by any number of frames shows; the left side climbs, the right side falls.
 */
public class TestDelayLine {

	private static final int FRAMES = FakeProcessors.FRAMES;

	private final TGDelayLine line = new TGDelayLine();

	// the stream's frame on a side, from 0; silence before the stream began
	private static float stream(int side, long frame) {
		if (frame < 0) {
			return 0f;
		}
		float level = ((frame % 1000) + 1) / 2000f;
		return (side == 0 ? level : -level);
	}

	// the block of the stream through the line, delayed by the frames
	private float[][] block(int block, int delay) {
		return block(this.line, block, delay);
	}

	private static float[][] block(TGDelayLine line, int block, int delay) {
		TGAudioBuffer buffer = shifted(block, 0);
		line.delay(buffer, delay);
		return FakeProcessors.read(buffer);
	}

	// what the block holds when the stream is shifted by the frames, as the 16-bit buffer rounds it
	private static float[][] expected(int block, int shift) {
		return FakeProcessors.read(shifted(block, shift));
	}

	private static TGAudioBuffer shifted(int block, int shift) {
		float[][] samples = new float[TGAudioBuffer.CHANNELS][FRAMES];
		for (int side = 0; side < samples.length; side++) {
			for (int i = 0; i < FRAMES; i++) {
				samples[side][i] = stream(side, (long) block * FRAMES + i - shift);
			}
		}
		TGAudioBuffer buffer = new TGAudioBuffer();
		buffer.write(samples);
		return buffer;
	}

	private static void assertBlock(float[][] expected, float[][] block) {
		assertArrayEquals(expected[0], block[0]);
		assertArrayEquals(expected[1], block[1]);
	}

	@Test
	public void eachBlockIsTheStreamThatManyFramesEarlierSilentBeforeItBegan() {
		for (int delay : new int[] {256, 700}) {
			TGDelayLine fresh = new TGDelayLine();
			for (int b = 0; b < 3; b++) {
				assertBlock(expected(b, delay), block(fresh, b, delay));
			}
		}
	}

	// growing from 0 to 256 leaves a gap of 256 silent frames; shrinking back to 0 drops the 256 frames held, so growing
	// again leaves a gap of silence again
	@Test
	public void aGrowingDelayFillsTheGapWithSilenceAndAShrinkingOneDropsTheOldestFrames() {
		block(0, 0);
		float[][] grown = block(1, 256);
		float[][] shrunk = block(2, 0);
		float[][] regrown = block(3, 256);
		assertBlock(gap(1), grown);
		assertBlock(expected(2, 0), shrunk);
		assertBlock(gap(3), regrown);
	}

	// growing from 256 to 700 frames while holding 256 keeps them, newest last, after 444 frames of silence
	@Test
	public void aDelayGrowingWhileItHoldsFramesKeepsThemAfterTheGap() {
		block(0, 256);
		block(1, 256);
		assertBlock(gap(2, 700, 444), block(2, 700));
		assertBlock(expected(3, 700), block(3, 700));
	}

	// shrinking from 700 to 256 frames drops the 444 oldest frames held: the next block is the stream 256 frames earlier
	@Test
	public void aShrinkingDelayLongerThanABlockDropsTheOldestFrames() {
		block(0, 700);
		block(1, 700);
		assertBlock(expected(2, 256), block(2, 256));
		assertBlock(expected(3, 256), block(3, 256));
	}

	// the line runs on the audio thread, which must not allocate per block: once the delay is set, it allocates nothing
	@Test
	public void delayingAllocatesNothingPerBlockOnceTheDelayIsSet() {
		com.sun.management.ThreadMXBean threads = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
		TGAudioBuffer buffer = shifted(0, 0);
		this.line.delay(buffer, 700);
		long before = threads.getThreadAllocatedBytes(Thread.currentThread().getId());
		for (int b = 0; b < 1000; b++) {
			this.line.delay(buffer, 700);
		}
		long allocated = threads.getThreadAllocatedBytes(Thread.currentThread().getId()) - before;
		assertTrue(allocated < 64 * 1024, allocated + " bytes allocated by 1000 blocks");
	}

	// the block delayed by 256 frames after 256 frames of silence
	private static float[][] gap(int block) {
		return gap(block, 256, 256);
	}

	// the block delayed by the frames, its first frames silent
	private static float[][] gap(int block, int delay, int silent) {
		float[][] gap = expected(block, delay);
		for (int side = 0; side < gap.length; side++) {
			java.util.Arrays.fill(gap[side], 0, silent, 0f);
		}
		return gap;
	}
}
