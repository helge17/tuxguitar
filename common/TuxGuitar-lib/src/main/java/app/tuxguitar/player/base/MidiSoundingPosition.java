package app.tuxguitar.player.base;

/**
 * The tick each frame of a sample clock is at, as its owner played it: anchors, each saying from which frame the song
 * is at which tick, moving on so many ticks every so many frames (a tempo), until the next anchor. Its owner anchors
 * where playing starts, where the tempo changes and where the song jumps. The output sounds a frame its sounding delay
 * after the clock reached it, so the tick sounding now is the tick at the frame that many frames back; a frame before
 * the oldest anchor is before playing started there, at that anchor's tempo run back (a pre-start position).
 *
 * The anchors are kept in a ring of CAPACITY, allocated once: the owner marks and reads it on the audio thread without
 * allocating. Past its capacity the oldest anchors are forgotten, so a frame more than CAPACITY anchors back is read
 * from the oldest one kept. One writer: the clock's thread.
 */
public final class MidiSoundingPosition {

	public static final int CAPACITY = 64;

	private final long[] frames = new long[CAPACITY];
	private final long[] ticks = new long[CAPACITY];
	private final double[] tempoTicks = new double[CAPACITY];
	private final double[] tempoFrames = new double[CAPACITY];
	// where the newest anchor is in the ring, and how many are kept
	private int newest = -1;
	private int count;

	// forgets the anchors, then anchors there (playing starts or moves)
	public void restart(long frame, long tick, double ticks, double frames) {
		this.count = 0;
		this.anchor(frame, tick, ticks, frames);
	}

	// from the frame on, the song is at the tick and moves on the ticks every so many frames; an anchor on the newest
	// one's frame replaces it
	public void anchor(long frame, long tick, double ticks, double frames) {
		if (this.count == 0 || this.frames[this.newest] != frame) {
			this.newest = (this.newest + 1) % CAPACITY;
			this.count = Math.min(this.count + 1, CAPACITY);
		}
		this.frames[this.newest] = frame;
		this.ticks[this.newest] = tick;
		this.tempoTicks[this.newest] = ticks;
		this.tempoFrames[this.newest] = frames;
	}

	// the tick at the frame, from the newest anchor at or before it (the oldest kept when none is); 0 without anchors
	public long tickAt(long frame) {
		if (this.count == 0) {
			return 0;
		}
		int index = this.newest;
		for (int i = 1; i < this.count && this.frames[index] > frame; i++) {
			index = (index + CAPACITY - 1) % CAPACITY;
		}
		return this.ticks[index] + (long) Math.floor((frame - this.frames[index]) * this.tempoTicks[index] / this.tempoFrames[index]);
	}
}
