package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

/**
 * Taking a block of a side: from inside it, up to its end, and past it (a chain running on after the tap ends).
 */
public class TestBlocks {

	private static final float[] SIDE = {1f, 2f, 3f, 4f, 5f};

	private static float[] take(long start) {
		float[] block = new float[3];
		TGBlocks.take(SIDE, start, block);
		return block;
	}

	@Test
	public void aBlockTakesTheSamplesFromItsStartAndNothingPastTheEnd() {
		assertArrayEquals(new float[] {2f, 3f, 4f}, take(1));
		assertArrayEquals(new float[] {4f, 5f, 0f}, take(3));
		assertArrayEquals(new float[] {5f, 0f, 0f}, take(4));
		assertArrayEquals(new float[] {0f, 0f, 0f}, take(5));
		assertArrayEquals(new float[] {0f, 0f, 0f}, take(9));
	}
}
