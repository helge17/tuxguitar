package app.tuxguitar.audio;

import app.tuxguitar.song.models.TGMeasureHeader;

/**
 * A measure as the song plays it, from one tick to another of it (in ticks from its start). It starts a stretch when
 * song time jumps to it: a repeat, an alternate ending, the jump back of a loop. Immutable.
 */
public final class TGPlayedMeasure {

	private final TGMeasureHeader header;
	private final long from;
	private final long to;
	private final boolean jump;

	public TGPlayedMeasure(TGMeasureHeader header, long from, long to, boolean jump) {
		this.header = header;
		this.from = from;
		this.to = to;
		this.jump = jump;
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
}
