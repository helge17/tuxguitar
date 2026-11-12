package app.tuxguitar.audio.features;

import app.tuxguitar.song.models.TGSong;

/**
 * Where a recording stops, as the features write it after "I record from <position>": "to <position>" (in the first
 * pass), "until the song ends" or "and do not stop it" (it stops by itself), "for <n> passes" (of Play looped), or
 * "and stop at <position> of the second pass".
 */
final class RecordingStop {

	// its groups: the position of "to", the passes, the position in the second pass
	static final String PATTERN = "to (" + SongPosition.PATTERN + ")|until the song ends|and do not stop it|for (\\d+) passes"
		+ "|and stop at (" + SongPosition.PATTERN + ") of the second pass";

	// none: it stops by itself, at the end of the song or of its last pass
	private final SongPosition position;
	private final int passes;

	private RecordingStop(SongPosition position, int passes) {
		this.position = position;
		this.passes = passes;
	}

	// from the groups of the pattern, null where they did not match
	static RecordingStop of(String to, String passes, String inTheSecondPass) {
		if (passes != null) {
			return new RecordingStop(null, Integer.parseInt(passes));
		}
		if (inTheSecondPass != null) {
			return new RecordingStop(SongPosition.parse(inTheSecondPass), 2);
		}
		return new RecordingStop(to != null ? SongPosition.parse(to) : null, 1);
	}

	// in ticks from the song start; Long.MAX_VALUE when it stops by itself
	long tick(TGSong song) {
		return (this.position != null ? this.position.tick(song) : Long.MAX_VALUE);
	}

	int passes() {
		return this.passes;
	}
}
