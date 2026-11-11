package app.tuxguitar.thread;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import app.tuxguitar.util.TGException;

/**
 * Pins TGExclusiveThreadHandler.loop: on the calling thread, it runs the loop step until the step returns BREAK,
 * waiting the number of milliseconds each step returns in between (no wait for null or a value below 1).
 * An interrupted wait ends the loop with a TGException. start runs a runnable on a new thread, at the highest
 * priority for HIGH and the normal one otherwise.
 */
@Timeout(10)
public class TestExclusiveThreadHandler {

	// a loop that returns the given timeouts in turn, and records when each step ran
	private static final class Steps implements TGThreadLoop {
		private final Iterator<Long> timeouts;
		private final List<Long> times = new ArrayList<Long>();

		Steps(Long... timeouts) {
			this.timeouts = Arrays.asList(timeouts).iterator();
		}

		public Long process() {
			this.times.add(System.nanoTime());
			return this.timeouts.next();
		}

		long millisBetween(int step, int nextStep) {
			return Duration.ofNanos(this.times.get(nextStep) - this.times.get(step)).toMillis();
		}
	}

	@Test
	public void runsTheStepUntilItBreaks() {
		Steps steps = new Steps(null, 0L, -5L, TGThreadLoop.BREAK, 1L);
		new TGExclusiveThreadHandler().loop(steps);

		assertEquals(4, steps.times.size());
	}

	@Test
	public void waitsTheReturnedTimeBetweenSteps() {
		Steps steps = new Steps(50L, TGThreadLoop.BREAK);
		new TGExclusiveThreadHandler().loop(steps);

		assertTrue(steps.millisBetween(0, 1) >= 50, "waited " + steps.millisBetween(0, 1) + "ms");
	}

	@Test
	public void anInterruptedWaitEndsTheLoop() {
		Steps steps = new Steps(1L, TGThreadLoop.BREAK);
		Thread.currentThread().interrupt();
		try {
			TGException exception = assertThrows(TGException.class, () -> new TGExclusiveThreadHandler().loop(steps));
			assertInstanceOf(InterruptedException.class, exception.getCause());
			assertEquals(1, steps.times.size());
		} finally {
			Thread.interrupted();
		}
	}

	// the priority of the new thread that ran the runnable
	private static int startedPriority(TGThreadPriority priority) throws InterruptedException {
		CompletableFuture<Integer> started = new CompletableFuture<Integer>();
		Thread caller = Thread.currentThread();
		new TGExclusiveThreadHandler().start(priority, () ->
			started.complete(Thread.currentThread() == caller ? -1 : Thread.currentThread().getPriority()));
		return started.join();
	}

	@Test
	public void startsTheRunnableOnANewThread() throws InterruptedException {
		assertEquals(Thread.MAX_PRIORITY, startedPriority(TGThreadPriority.HIGH));
		assertEquals(Thread.NORM_PRIORITY, startedPriority(TGThreadPriority.NORMAL));
	}
}
