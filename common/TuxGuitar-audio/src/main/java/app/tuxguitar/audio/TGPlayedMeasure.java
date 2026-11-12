package app.tuxguitar.audio;

import app.tuxguitar.song.models.TGMeasureHeader;

/**
 * A measure as the song plays it, from one tick to another of it (in ticks from its start), in a pass (from 0) at the
 * Percentage of Tempo of that pass. It starts a stretch when song time jumps to it: a repeat, an alternate ending, the
 * jump back of a loop. Immutable.
 */
public final class TGPlayedMeasure {

	private final TGMeasureHeader header;
	private final long from;
	private final long to;
	private final boolean jump;
	private final int pass;
	private final int percent;
	private final int step;

	// step: where it comes in its pass, from 0
	public TGPlayedMeasure(TGMeasureHeader header, long from, long to, boolean jump, int pass, int percent, int step) {
		this.header = header;
		this.from = from;
		this.to = to;
		this.jump = jump;
		this.pass = pass;
		this.percent = percent;
		this.step = step;
	}

	public int getStep() {
		return this.step;
	}

	public TGMeasureHeader getHeader() {
		return this.header;
	}

	public long getFrom() {
		return this.from;
	}

	public long getTo() {
		return this.to;
	}

	public boolean isJump() {
		return this.jump;
	}

	public int getPass() {
		return this.pass;
	}

	public int getPercent() {
		return this.percent;
	}

	// quarter notes per minute as it sounds
	public double getTempo() {
		return TGSampleTime.tempo(this.header, this.percent);
	}
}
