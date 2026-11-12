package app.tuxguitar.song.models;

/**
 * The part of a take that sits in one measure: from start to end, in ticks from the measure's start (quarter-note
 * time), its audio starting at a sample of the take, recorded at a tempo (quarter notes per minute as the song sounded,
 * the Percentage of Tempo included). A take is anchored measure by measure, so it follows its measures as notes do;
 * played at another tempo it is time-stretched to stay on its beats. Immutable.
 */
public final class TGTakeClip {

	private final TGTake take;
	private final TGTake source;
	private final long start;
	private final long end;
	private final int firstSample;
	private final double tempo;

	public TGTakeClip(TGTake take, long start, long end, int firstSample, double tempo) {
		this(take, take, start, end, firstSample, tempo);
	}

	// a clip of the take that plays the samples of another take (the source), as pasted audio does
	public TGTakeClip(TGTake take, TGTake source, long start, long end, int firstSample, double tempo) {
		this.take = take;
		this.source = source;
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

	// the take whose samples the clip plays: its own take, unless pasted
	public TGTake getSource() {
		return this.source;
	}

	// the sample of the source where the clip's start sits
	public int getFirstSample() {
		return this.firstSample;
	}

	public double getTempo() {
		return this.tempo;
	}

	// the sample of the source where the tick sits, counted at the tempo the clip was recorded at
	public int sampleAt(long tick) {
		return this.firstSample + samples(tick - this.start, this.tempo, this.source.getSampleRate());
	}

	// how many samples the ticks last at the tempo (quarter notes per minute)
	public static int samples(long ticks, double tempo, int sampleRate) {
		return (int) Math.round(ticks * 60.0 * sampleRate / (TGDuration.QUARTER_TIME * tempo));
	}

	// the part from one tick to another, as a clip of another take that plays these samples
	public TGTakeClip copy(TGTake take, long start, long end) {
		return new TGTakeClip(take, this.source, start, end, this.sampleAt(start), this.tempo);
	}
}
