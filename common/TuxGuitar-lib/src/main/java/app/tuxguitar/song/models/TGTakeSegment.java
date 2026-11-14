package app.tuxguitar.song.models;


/**
 * Where, in ticks from a measure's start, a take starts playing there; the take is null where none plays.
 */
public final class TGTakeSegment {

	private final long start;
	private final TGTake take;

	public TGTakeSegment(long start, TGTake take) {
		this.start = start;
		this.take = take;
	}

	public long getStart() {
		return this.start;
	}

	public TGTake getTake() {
		return this.take;
	}
}
