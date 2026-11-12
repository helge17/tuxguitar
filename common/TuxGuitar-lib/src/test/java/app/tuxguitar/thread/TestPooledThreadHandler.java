package app.tuxguitar.thread;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pins TGPooledThreadHandler: ten worker threads run the started runnables, in no promised order; a loop is run
 * again, after waiting its timeout, until it answers BREAK. Once disposed, the workers finish what is queued and end.
 *
 * The handler is created from a thread of a group of its own, so its workers (which join the group of the thread
 * creating them) can be told apart. Every wait is bounded; nothing sleeps.
 */
public class TestPooledThreadHandler {

	private static final long TIMEOUT_SECONDS = 10;

	private final ThreadGroup group = new ThreadGroup("pooled workers");
	private TGPooledThreadHandler handler;

	@BeforeEach
	public void createHandler() throws InterruptedException {
		Thread creator = new Thread(this.group, () -> this.handler = new TGPooledThreadHandler());
		creator.start();
		creator.join();
	}

	@AfterEach
	public void disposeHandler() {
		this.handler.dispose();
	}

	private List<Thread> workers() {
		Thread[] threads = new Thread[this.group.activeCount() + 10];
		int count = this.group.enumerate(threads);
		return Arrays.asList(threads).subList(0, count);
	}

	private static void await(CountDownLatch latch) throws InterruptedException {
		assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), "timed out");
	}

	// disposes the handler, then (once the given action is done) waits for its workers to end: the workers still alive
	private List<Thread> disposeAndJoin(Runnable afterDispose) throws InterruptedException {
		List<Thread> workers = workers();
		this.handler.dispose();
		afterDispose.run();
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(TIMEOUT_SECONDS);
		List<Thread> alive = new ArrayList<Thread>();
		for (Thread worker : workers) {
			worker.join(Math.max(1, TimeUnit.NANOSECONDS.toMillis(deadline - System.nanoTime())));
			if (worker.isAlive()) {
				alive.add(worker);
			}
		}
		return alive;
	}

	@Test
	public void startsTenWorkers() {
		assertEquals(10, workers().size());
	}

	@Test
	public void runsEveryStartedRunnableOnAWorker() throws InterruptedException {
		int count = 25;
		CountDownLatch done = new CountDownLatch(count);
		List<ThreadGroup> groups = Collections.synchronizedList(new ArrayList<ThreadGroup>());
		for (int i = 0; i < count; i++) {
			this.handler.start(TGThreadPriority.NORMAL, () -> {
				groups.add(Thread.currentThread().getThreadGroup());
				done.countDown();
			});
		}

		await(done);

		assertEquals(Collections.nCopies(count, this.group), groups);
	}

	@Test
	public void loopsUntilBreak() throws InterruptedException {
		List<Long> answers = new ArrayList<Long>(Arrays.asList(0L, null, 1L, TGThreadLoop.BREAK));
		AtomicInteger calls = new AtomicInteger();
		CountDownLatch broke = new CountDownLatch(1);
		this.handler.loop(() -> {
			calls.incrementAndGet();
			Long answer = answers.remove(0);
			if (answers.isEmpty()) {
				broke.countDown();
			}
			return answer;
		});

		await(broke);

		assertEquals(List.of(), disposeAndJoin(() -> { }));
		assertEquals(4, calls.get());
	}

	@Test
	public void disposedWorkersFinishTheQueueAndEnd() throws InterruptedException {
		CountDownLatch release = new CountDownLatch(1);
		AtomicInteger ran = new AtomicInteger();
		for (int i = 0; i < 12; i++) {
			this.handler.start(TGThreadPriority.NORMAL, () -> {
				try {
					release.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				ran.incrementAndGet();
			});
		}
		List<Thread> alive = disposeAndJoin(release::countDown);

		assertEquals(12, ran.get());
		assertEquals(List.of(), alive);
	}
}
