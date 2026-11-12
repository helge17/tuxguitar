package app.tuxguitar.audio;

import app.tuxguitar.song.models.TGTake;

/**
 * A take and the stretch of the song its clips cover, from the start of the first to the end of the last, in ticks
 * from the song start. Measures inserted inside a take are spanned over.
 */
public final class TGTakeSpan {

	private final TGTake take;
	private final long start;
	private final long end;

	public TGTakeSpan(TGTake take, long start, long end) {
		this.take = take;
		this.start = start;
		this.end = end;
	}

	public TGTake getTake() {
		return this.take;
	}

	public long getStart() {
		return this.start;
	}

	public long getEnd() {
		return this.end;
	}
}
