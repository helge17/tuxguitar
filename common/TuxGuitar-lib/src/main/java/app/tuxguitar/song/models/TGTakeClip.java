package app.tuxguitar.song.models;

/**
 * The part of a take that sits in one measure: from start to end, in ticks from the measure's start (quarter-note
 * time), its audio starting at a sample of the take, recorded at a tempo (quarter notes per minute as the song sounded,
 * the Percentage of Tempo included). A take is anchored measure by measure, so it follows its measures as notes do;
 * played at another tempo it is time-stretched to stay on its beats. Immutable.
 */
public final class TGTakeClip {

	private final TGTake take;
	private final long start;
	private final long end;
	private final int firstSample;
	private final double tempo;

	public TGTakeClip(TGTake take, long start, long end, int firstSample, double tempo) {
		this.take = take;
		this.start = start;
		this.end = end;
		this.firstSample = firstSample;
		this.tempo = tempo;
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

	// the sample of the take where the clip's start sits
	public int getFirstSample() {
		return this.firstSample;
	}

	public double getTempo() {
		return this.tempo;
	}
}
