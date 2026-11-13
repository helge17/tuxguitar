package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.List;

/**
 * The Metronome's clicks over a timeline: one on each beat (the time signature's denominator) of each measure played,
 * at the sample where its note reaches the TuxGuitar Synthesizer's General MIDI drums (outside every sound chain, see
 * TGDrumTap), each held a beat at its measure's tempo.
 */
public final class TGMetronome {

	public static final TGMetronome NONE = new TGMetronome(new long[0], new long[0]);

	private final long[] positions;
	private final long[] holds;

	private TGMetronome(long[] positions, long[] holds) {
		this.positions = positions;
		this.holds = holds;
	}

	public static TGMetronome of(TGTimeline timeline) {
		List<Long> positions = new ArrayList<Long>();
		List<Long> holds = new ArrayList<Long>();
		for (int index = 0; index < timeline.count(); index++) {
			TGPlayedMeasure measure = timeline.get(index);
			long beat = measure.getHeader().getTimeSignature().getDenominator().getTime();
			// the first beat from where the measure starts playing
			for (long tick = (measure.getFrom() + beat - 1) / beat * beat; tick < measure.getTo(); tick += beat) {
				positions.add(timeline.sampleAt(index, tick));
				holds.add(timeline.sampleAt(index, tick + beat) - timeline.sampleAt(index, tick));
			}
		}
		return new TGMetronome(array(positions), array(holds));
	}

	private static long[] array(List<Long> values) {
		return values.stream().mapToLong(Long::longValue).toArray();
	}

	// where each click's note reaches the drums
	public long[] positions() {
		return this.positions.clone();
	}

	// how long the click is held
	public long hold(int click) {
		return this.holds[click];
	}
}
