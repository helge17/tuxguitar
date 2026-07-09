package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestElapsedTimeLoopAccumulator {

	@Test
	public void onAccumulateWhenTimestampWrapsExpectContinuousElapsedTime() {
		ElapsedTimeLoopAccumulator accumulator = new ElapsedTimeLoopAccumulator();

		assertEquals(120L, accumulator.accumulate(120L));
		assertEquals(380L, accumulator.accumulate(380L));
		assertEquals(430L, accumulator.accumulate(50L));
	}

	@Test
	public void onResetWhenNewPlaybackStartsExpectElapsedTimeStartsFromLoopTimestamp() {
		ElapsedTimeLoopAccumulator accumulator = new ElapsedTimeLoopAccumulator();

		accumulator.accumulate(900L);
		accumulator.accumulate(20L);
		accumulator.reset();

		assertEquals(40L, accumulator.accumulate(40L));
	}
}
