package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * The input level: the peak of the last second only, the higher side of a pair, clipping from -0.1 dBFS, and the clip
 * indicator held until reset. The meter runs at 4 samples a second, so a second is 4 samples.
 */
public class TestInputMeter {

	private final TGInputMeter meter = new TGInputMeter(4);

	private static float[][] level(int samples, float... sides) {
		float[][] block = new float[sides.length][samples];
		for (int side = 0; side < sides.length; side++) {
			Arrays.fill(block[side], sides[side]);
		}
		return block;
	}

	@Test
	public void silenceReadsMinusInfinity() {
		assertEquals(Double.NEGATIVE_INFINITY, meter.getLevel());
	}

	@Test
	public void aPeakCountsForOneSecond() {
		meter.deliver(level(1, -0.5f));
		meter.deliver(level(3, 0.1f));
		assertEquals(20 * Math.log10(0.5), meter.getLevel(), 1e-6);

		meter.deliver(level(1, 0.1f));
		assertEquals(20 * Math.log10(0.1f), meter.getLevel(), 1e-6);
	}

	@Test
	public void aPairReadsItsHigherSide() {
		meter.deliver(level(2, 0.25f, -0.5f));
		assertEquals(20 * Math.log10(0.5), meter.getLevel(), 1e-6);
	}

	@Test
	public void theInputIsClippingFromMinusOneTenthOfADecibel() {
		meter.deliver(level(1, (float) Math.pow(10, -0.1001 / 20)));
		assertFalse(meter.isClipping());
		assertFalse(meter.hasClipped());

		meter.deliver(level(1, (float) Math.pow(10, -0.0999 / 20)));
		assertTrue(meter.isClipping());
		assertTrue(meter.hasClipped());
	}

	@Test
	public void theClipIndicatorHoldsUntilReset() {
		meter.deliver(level(1, 1f));
		meter.deliver(level(8, 0f));
		assertFalse(meter.isClipping());
		assertTrue(meter.hasClipped());

		meter.resetClipIndicator();
		assertFalse(meter.hasClipped());
	}
}
