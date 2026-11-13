package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;

/**
 * What plays, on a sample clock that starts where playing starts: the Count In, if any, then the measures as they
 * play, where each starts and how many samples it lasts at its tempo and Percentage of Tempo. The audio device's clock
 * counts from the start of the song, so the timeline also knows the device's clock where playing starts. Immutable.
 */
public final class TGTimeline {

	private final List<TGPlayedMeasure> measures;
	private final long[] starts;
	private final long clock;
	private final int sampleRate;
	private final TGCountIn countIn;

	public TGTimeline(TGSong song, List<TGPlayedMeasure> measures, int sampleRate) {
		this(song, measures, sampleRate, TGCountIn.NONE);
	}

	// the measures after the Count In
	public TGTimeline(TGSong song, List<TGPlayedMeasure> measures, int sampleRate, TGCountIn countIn) {
		this(measures, sampleRate, countIn, (measures.isEmpty() ? 0 : clockAt(song, measures.get(0), sampleRate) - countIn.length()));
	}

	private TGTimeline(List<TGPlayedMeasure> measures, int sampleRate, TGCountIn countIn, long clock) {
		this.measures = new ArrayList<TGPlayedMeasure>(measures);
		this.sampleRate = sampleRate;
		this.countIn = countIn;
		this.clock = clock;
		this.starts = new long[measures.size() + 1];
		this.starts[0] = countIn.length();
		for (int i = 0; i < measures.size(); i++) {
			TGPlayedMeasure measure = measures.get(i);
			this.starts[i + 1] = this.starts[i] + samples(measure.getTo() - measure.getFrom(), measure, sampleRate);
		}
	}

	// the device's clock where the first measure played starts: the measures before it, whole, and its beats before
	// where playing starts, at its tempo
	private static long clockAt(TGSong song, TGPlayedMeasure first, int sampleRate) {
		long clock = samples(first.getFrom(), first, sampleRate);
		for (int index = 0; index < first.getHeader().getNumber() - 1; index++) {
			TGMeasureHeader header = song.getMeasureHeader(index);
			clock += TGSampleTime.samples(header.getLength(), TGSampleTime.tempo(header, first.getPercent()), sampleRate);
		}
		return clock;
	}

	private static int samples(long ticks, TGPlayedMeasure measure, int sampleRate) {
		return TGSampleTime.samples(ticks, measure.getTempo(), sampleRate);
	}

	// what plays before the sample: the Count In up to it, the measures that start before it, each up to the first of
	// its ticks that sounds at the sample or after (its end, for a measure that ends before the sample)
	public TGTimeline until(long sample) {
		List<TGPlayedMeasure> kept = new ArrayList<TGPlayedMeasure>();
		for (int i = 0; i < this.count() && this.start(i) < sample; i++) {
			kept.add(this.get(i).until(this.tickAt(i, sample)));
		}
		return new TGTimeline(kept, this.sampleRate, this.countIn.until(sample), this.clock);
	}

	// the first tick of the played measure that sounds at the sample or after
	private long tickAt(int index, long sample) {
		long low = this.get(index).getFrom();
		long high = this.get(index).getTo();
		while (low < high) {
			long middle = (low + high) / 2;
			if (this.sampleAt(index, middle) < sample) {
				low = middle + 1;
			} else {
				high = middle;
			}
		}
		return low;
	}

	public int getSampleRate() {
		return this.sampleRate;
	}

	// the audio device's clock where playing starts
	public long getClock() {
		return this.clock;
	}

	public TGCountIn getCountIn() {
		return this.countIn;
	}

	public int count() {
		return this.measures.size();
	}

	public TGPlayedMeasure get(int index) {
		return this.measures.get(index);
	}

	public long start(int index) {
		return this.starts[index];
	}

	public long end(int index) {
		return this.starts[index + 1];
	}

	public long length() {
		return this.starts[this.measures.size()];
	}

	// where a tick of the played measure sounds, counted from where the measure starts playing
	public long sampleAt(int index, long tick) {
		return this.starts[index] + samples(tick - this.measures.get(index).getFrom(), this.measures.get(index), this.sampleRate);
	}
}
