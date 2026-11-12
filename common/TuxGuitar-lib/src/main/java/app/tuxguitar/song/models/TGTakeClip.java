package app.tuxguitar.song.models;

/**
 * The part of a take that sits in one measure: from start to end, in ticks from the measure's start (quarter-note
 * time). A take is anchored measure by measure, so it follows its measures as notes do. Immutable.
 */
public final class TGTakeClip {

	private final TGTake take;
	private final long start;
	private final long end;

	public TGTakeClip(TGTake take, long start, long end) {
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
