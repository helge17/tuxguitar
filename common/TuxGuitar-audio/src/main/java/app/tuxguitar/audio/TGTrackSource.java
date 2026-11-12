package app.tuxguitar.audio;

/**
 * What an audio track sounds in a played measure: its takes, its monitored input instead, or nothing (an armed track
 * where it records, with monitoring off).
 */
public enum TGTrackSource {
	TAKES, INPUT, SILENCE
}
