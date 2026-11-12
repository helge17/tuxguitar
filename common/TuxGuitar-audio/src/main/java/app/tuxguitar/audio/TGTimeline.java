package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;

/**
 * The measures as they play, on a sample clock that starts where playing starts: where each played measure starts and
 * how many samples it lasts, at its tempo and Percentage of Tempo. The audio device's clock counts from the start of
 * the song, so the timeline also knows the device's clock where playing starts. Immutable.
 */
public final class TGTimeline {

	private final List<TGPlayedMeasure> measures;
	private final long[] starts;
	private final long clock;
	private final int sampleRate;

	public TGTimeline(TGSong song, List<TGPlayedMeasure> measures, int sampleRate) {
		this.measures = new ArrayList<TGPlayedMeasure>(measures);
		this.sampleRate = sampleRate;
		this.starts = new long[measures.size() + 1];
		for (int i = 0; i < measures.size(); i++) {
			TGPlayedMeasure measure = measures.get(i);
			this.starts[i + 1] = this.starts[i] + this.samples(measure.getTo() - measure.getFrom(), measure);
		}
		this.clock = (measures.isEmpty() ? 0 : this.clockAt(song, measures.get(0)));
	}

	// the measures before the first one played, whole, and its beats before where playing starts, at its tempo
	private long clockAt(TGSong song, TGPlayedMeasure first) {
		long clock = this.samples(first.getFrom(), first);
		for (int index = 0; index < first.getHeader().getNumber() - 1; index++) {
			TGMeasureHeader header = song.getMeasureHeader(index);
			clock += TGSampleTime.samples(header.getLength(), TGSampleTime.tempo(header, first.getPercent()), this.sampleRate);
		}
		return clock;
	}

	private int samples(long ticks, TGPlayedMeasure measure) {
		return TGSampleTime.samples(ticks, measure.getTempo(), this.sampleRate);
	}

	public int getSampleRate() {
		return this.sampleRate;
	}

	// the audio device's clock where playing starts
	public long getClock() {
		return this.clock;
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
		return this.starts[index] + this.samples(tick - this.measures.get(index).getFrom(), this.measures.get(index));
	}
}
