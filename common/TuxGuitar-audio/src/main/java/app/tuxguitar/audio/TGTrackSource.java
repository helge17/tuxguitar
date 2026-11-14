package app.tuxguitar.audio;

import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;

/**
 * What an audio track sounds in a played measure: its takes, its monitored input instead, or nothing (an armed track
 * where it records, with monitoring off). Offline and live alike.
 */
public enum TGTrackSource {
	TAKES, INPUT, SILENCE;

	// in each played measure of the timeline: where the track records (armed, while recording, inside the punch range or
	// everywhere without one) its input (monitoring Auto or Always) or nothing (Off); with monitoring Always an armed
	// track sounds its input everywhere else too; elsewhere its takes
	static TGTrackSource[] of(TGAudioSession session, TGSong song, TGTrack track, TGTimeline timeline, boolean recording, boolean armed) {
		TGMonitoring monitoring = session.getMonitoring(track);
		TGPunchRange punchRange = session.getPunchRange(song);
		TGTrackSource[] sources = new TGTrackSource[timeline.count()];
		for (int i = 0; i < sources.length; i++) {
			boolean records = recording && armed && (punchRange == null || punchRange.contains(timeline.get(i).getHeader().getNumber()));
			sources[i] = of(records, armed, monitoring);
		}
		return sources;
	}

	private static TGTrackSource of(boolean records, boolean armed, TGMonitoring monitoring) {
		if (records) {
			return (monitoring == TGMonitoring.OFF ? SILENCE : INPUT);
		}
		return (armed && monitoring == TGMonitoring.ALWAYS ? INPUT : TAKES);
	}

	// while stopped: an armed track monitored Auto or Always sounds its input; nothing else sounds
	static TGTrackSource stopped(boolean armed, TGMonitoring monitoring) {
		return (armed && monitoring != TGMonitoring.OFF ? INPUT : SILENCE);
	}
}
