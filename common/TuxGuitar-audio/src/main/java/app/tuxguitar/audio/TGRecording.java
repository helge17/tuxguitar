package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;

/**
 * A recording of measures as the song played them: for each armed track, what its input channel delivered inside the
 * punch range (everywhere, without one), one take per stretch of song time without a jump.
 */
public final class TGRecording {

	// measures kept in a row, and the device's clock where the first starts
	private static final class Stretch {
		private final long clock;
		private final List<TGPlayedMeasure> measures = new ArrayList<TGPlayedMeasure>();

		private Stretch(long clock) {
			this.clock = clock;
		}
	}

	private final TGSongManager songManager;
	private final TGSong song;
	private final TGAudioDevice device;
	private final TGPunchRange punchRange;
	private final int percent;

	public TGRecording(TGSongManager songManager, TGSong song, TGAudioDevice device, TGPunchRange punchRange, int percent) {
		this.songManager = songManager;
		this.song = song;
		this.device = device;
		this.punchRange = punchRange;
		this.percent = percent;
	}

	// records the played measures on the tracks; returns how many takes it made
	public int record(List<TGTrack> tracks, List<TGPlayedMeasure> played) {
		if (played.isEmpty()) {
			return 0;
		}
		int takes = 0;
		for (Stretch stretch : stretches(played)) {
			takes += take(tracks, stretch);
		}
		return takes;
	}

	// the measures kept, grouped in stretches: a measure kept after one that was not, or after a jump, starts one
	private List<Stretch> stretches(List<TGPlayedMeasure> played) {
		List<Stretch> stretches = new ArrayList<Stretch>();
		Stretch stretch = null;
		long clock = clockAt(played.get(0));
		for (TGPlayedMeasure measure : played) {
			if (!isKept(measure)) {
				stretch = null;
			} else if (stretch == null || measure.isJump()) {
				stretch = new Stretch(clock);
				stretches.add(stretch);
			}
			if (stretch != null) {
				stretch.measures.add(measure);
			}
			clock += samples(measure);
		}
		return stretches;
	}

	private boolean isKept(TGPlayedMeasure measure) {
		return (this.punchRange == null || this.punchRange.contains(measure.getHeader().getNumber()));
	}

	// the device's clock where the first played measure starts playing: it counts from the start of the song
	private long clockAt(TGPlayedMeasure first) {
		long clock = samples(first.getHeader(), first.getFrom());
		for (int index = 0; index < first.getHeader().getNumber() - 1; index++) {
			TGMeasureHeader header = this.song.getMeasureHeader(index);
			clock += samples(header, header.getLength());
		}
		return clock;
	}

	private int take(List<TGTrack> tracks, Stretch stretch) {
		int count = 0;
		for (TGPlayedMeasure measure : stretch.measures) {
			count += samples(measure);
		}
		for (TGTrack track : tracks) {
			int input = this.songManager.getChannel(this.song, track.getChannelId()).getAudioInput().getChannel();
			TGTakes.record(track, this.device.capture(input, stretch.clock, count), stretch.measures);
		}
		return tracks.size();
	}

	private int samples(TGPlayedMeasure measure) {
		return samples(measure.getHeader(), measure.getTo() - measure.getFrom());
	}

	// how many samples the ticks of the measure last, at its tempo and the Percentage of Tempo
	private int samples(TGMeasureHeader header, long ticks) {
		double seconds = (ticks * 60.0 * 100) / (TGDuration.QUARTER_TIME * header.getTempo().getQuarterValue() * (double) this.percent);
		return (int) Math.round(seconds * this.device.getSampleRate());
	}
}
