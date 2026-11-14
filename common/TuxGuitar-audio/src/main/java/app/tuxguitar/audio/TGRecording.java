package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;

/**
 * A recording of measures as the song played them, on the session's audio device: for each armed track, what its input
 * channel delivered inside the session's punch range (everywhere, without one), one take per stretch of song time
 * without a jump. What was played in time with the output reaches the input one round trip after the clock that played
 * it (the output sounds it the plugin delay compensation of what is heard and the output latency later): each take is
 * read that much later, so it is placed earlier by its round trip and lands on the beat (and the
 * recording goes on taking input for one round trip past where it stops). What the inputs delivered over the timeline
 * (its Count In and its measures, kept or not) feeds the armed tracks' input meters.
 */
public final class TGRecording {

	private final TGSongManager songManager;
	private final TGSong song;
	private final TGAudioSession session;
	private final TGAudioDevice device;
	private final TGPunchRange punchRange;
	private final int compensation;
	private final int roundTrip;

	// compensation: the plugin delay compensation of what is heard while recording (the render's latency)
	public TGRecording(TGSongManager songManager, TGSong song, TGAudioSession session, int compensation) {
		this(songManager, song, session, session.getDevice(), compensation);
	}

	// the input read from the device given (its clock the timeline's), the latencies from the session's
	TGRecording(TGSongManager songManager, TGSong song, TGAudioSession session, TGAudioDevice device, int compensation) {
		this.songManager = songManager;
		this.song = song;
		this.session = session;
		this.device = device;
		this.punchRange = session.getPunchRange(song);
		this.compensation = compensation;
		this.roundTrip = session.getRoundTrip(compensation);
	}

	// records the measures of the timeline on the tracks, offline: what the inputs delivered meanwhile feeds the input
	// meters; returns how many takes it made
	public int record(List<TGTrack> tracks, TGTimeline timeline) {
		int takes = this.takes(tracks, timeline);
		this.session.meterPlayed(this.songManager, this.song, timeline, this.compensation);
		return takes;
	}

	// the takes alone (live, the input meters follow the device); how many
	int takes(List<TGTrack> tracks, TGTimeline timeline) {
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
		long clock = timeline.getClock() + timeline.start(first) + this.roundTrip;
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
