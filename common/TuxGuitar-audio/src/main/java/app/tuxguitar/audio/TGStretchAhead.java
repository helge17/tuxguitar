package app.tuxguitar.audio;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Makes the time-stretches of a live tap ahead of the audio thread, on a worker: after each block, those of the pieces
 * sounding within the next two seconds of the tap, so a block that reaches a piece finds its stretch made (one the
 * worker has not got to yet, the block makes itself, as before). The worker is given one task at a time: while it has
 * not run the last one, blocks give it none.
 */
final class TGStretchAhead {

	static final int SECONDS = 2;

	private final Executor worker;
	// whether the worker has a task it has not run yet
	private final AtomicBoolean given = new AtomicBoolean();

	TGStretchAhead(Executor worker) {
		this.worker = worker;
	}

	// the tap has played up to the sample (at the sample rate): the worker makes the stretches of the pieces sounding
	// from there over the next two seconds
	void after(TGTakeTap tap, long sample, int sampleRate) {
		if (this.given.compareAndSet(false, true)) {
			this.worker.execute(() -> {
				tap.prepare(sample, sample + (long) SECONDS * sampleRate);
				this.given.set(false);
			});
		}
	}
}
