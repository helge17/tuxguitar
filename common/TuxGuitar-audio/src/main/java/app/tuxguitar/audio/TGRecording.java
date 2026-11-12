package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;

/**
 * A recording of measures as the song played them: for each armed track, what its input channel delivered inside the
 * punch range (everywhere, without one), one take per stretch of song time without a jump.
 */
public final class TGRecording {

	private final TGSongManager songManager;
	private final TGSong song;
	private final TGAudioDevice device;
	private final TGPunchRange punchRange;

	public TGRecording(TGSongManager songManager, TGSong song, TGAudioDevice device, TGPunchRange punchRange) {
		this.songManager = songManager;
		this.song = song;
		this.device = device;
		this.punchRange = punchRange;
	}

	// records the played measures on the tracks; returns how many takes it made
	public int record(List<TGTrack> tracks, List<TGPlayedMeasure> played) {
		TGTimeline timeline = new TGTimeline(this.song, played, this.device.getSampleRate());
		int takes = 0;
		for (int[] stretch : stretches(timeline)) {
			takes += take(tracks, timeline, stretch[0], stretch[1]);
		}
		return takes;
	}

	// the measures kept, grouped in stretches (first and last index): a measure kept after one that was not, or after
	// a jump, starts one
	private List<int[]> stretches(TGTimeline timeline) {
		List<int[]> stretches = new ArrayList<int[]>();
		int[] stretch = null;
		for (int i = 0; i < timeline.count(); i++) {
			TGPlayedMeasure measure = timeline.get(i);
			if (!isKept(measure)) {
				stretch = null;
			} else if (stretch == null || measure.isJump()) {
				stretch = new int[] {i, i};
				stretches.add(stretch);
			}
			if (stretch != null) {
				stretch[1] = i;
			}
		}
		return stretches;
	}

	private boolean isKept(TGPlayedMeasure measure) {
		return (this.punchRange == null || this.punchRange.contains(measure.getHeader().getNumber()));
	}

	private int take(List<TGTrack> tracks, TGTimeline timeline, int first, int last) {
		long clock = timeline.getClock() + timeline.start(first);
		int count = (int) (timeline.end(last) - timeline.start(first));
		for (TGTrack track : tracks) {
			TGTakes.record(track, this.capture(track, clock, count), timeline, first, last);
		}
		return tracks.size();
	}

	private float[][] capture(TGTrack track, long clock, int count) {
		return this.device.capture(this.songManager.getChannel(this.song, track.getChannelId()).getAudioInput(), clock, count);
	}
}
