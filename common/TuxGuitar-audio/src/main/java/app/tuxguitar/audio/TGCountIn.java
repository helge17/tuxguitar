package app.tuxguitar.audio;

import java.util.List;
import java.util.stream.LongStream;

import app.tuxguitar.player.base.MidiPlayerCountDown;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGTimeSignature;

/**
 * Count In: clicks one beat apart before the first measure plays, the first one where playing starts and the last one
 * a beat before the first measure. One per beat of the measure playing starts in (or as many as the Count In ticks
 * set), a beat being the unit of its time signature at its tempo and the first pass's Percentage of Tempo, exactly.
 * Positions are samples from where playing starts. Stopped early, it lasts until the stop and keeps the clicks before
 * it. Immutable.
 */
public final class TGCountIn {

	public static final TGCountIn NONE = new TGCountIn(0, 0, 0);

	private final int clicks;
	// samples per beat, not rounded
	private final double beat;
	// samples before the first measure
	private final long length;

	private TGCountIn(int clicks, double beat, long length) {
		this.clicks = clicks;
		this.beat = beat;
		this.length = length;
	}

	// the Count In before the measures played, with Count In on (none with it off, or when nothing plays)
	public static TGCountIn before(MidiPlayerCountDown countDown, List<TGPlayedMeasure> played, int sampleRate) {
		if (!countDown.isEnabled() || played.isEmpty()) {
			return NONE;
		}
		TGPlayedMeasure first = played.get(0);
		TGTimeSignature signature = first.getHeader().getTimeSignature();
		int clicks = (countDown.getTickCount() != 0 ? countDown.getTickCount() : signature.getNumerator());
		double quarters = signature.getDenominator().getTime() / (double) TGDuration.QUARTER_TIME;
		double beat = quarters * 60.0 * sampleRate / first.getTempo();
		return new TGCountIn(clicks, beat, Math.round(clicks * beat));
	}

	// samples before the first measure
	public long length() {
		return this.length;
	}

	// how long a click is held: a beat
	public long hold() {
		return Math.round(this.beat);
	}

	// where each click's note reaches the drums: a beat apart from where playing starts, those before it stopped
	public long[] positions() {
		return LongStream.range(0, this.clicks).map(click -> Math.round(click * this.beat)).filter(position -> position < this.length).toArray();
	}

	// stopped at the sample
	public TGCountIn until(long sample) {
		return new TGCountIn(this.clicks, this.beat, Math.min(this.length, sample));
	}
}
