package app.tuxguitar.audio.features;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;

/**
 * Where a recording stops, as the features write it after "I record from <position>": "to <position>" (in the first
 * pass), "until the song ends" or "and do not stop it" (it stops by itself), "for <n> passes" (of Play looped),
 * "and stop at <position> of the second pass", or "and stop after the second Count In click": half a beat after it,
 * clear of its attack and of the third click (the Count In's first click is where playing starts, and a beat is the
 * one of the measure recording starts in at the Percentage of Tempo).
 */
final class RecordingStop {

	// its groups: the position of "to", the passes, the position in the second pass, the second Count In click
	static final String PATTERN = "to (" + SongPosition.PATTERN + ")|until the song ends|and do not stop it|for (\\d+) passes"
		+ "|and stop at (" + SongPosition.PATTERN + ") of the second pass|and stop (after the second Count In click)";

	// none: it stops by itself, at the end of the song or of its last pass
	private final SongPosition position;
	private final int passes;
	// beats after where playing starts, or none (NaN)
	private final double beats;

	private RecordingStop(SongPosition position, int passes, double beats) {
		this.position = position;
		this.passes = passes;
		this.beats = beats;
	}

	// from the groups of the pattern, null where they did not match
	static RecordingStop of(String to, String passes, String inTheSecondPass, String afterTheSecondClick) {
		if (passes != null) {
			return new RecordingStop(null, Integer.parseInt(passes), Double.NaN);
		}
		if (inTheSecondPass != null) {
			return new RecordingStop(SongPosition.parse(inTheSecondPass), 2, Double.NaN);
		}
		return new RecordingStop(to != null ? SongPosition.parse(to) : null, 1, (afterTheSecondClick != null ? 1.5 : Double.NaN));
	}

	// where it stops in what plays, in samples from where playing starts; Long.MAX_VALUE at a position or by itself
	long sample(SongPosition from, TGSong song, int percent, int sampleRate) {
		if (Double.isNaN(this.beats)) {
			return Long.MAX_VALUE;
		}
		TGMeasureHeader header = song.getMeasureHeader(from.measure() - 1);
		double quarters = header.getTimeSignature().getDenominator().getTime() / (double) TGDuration.QUARTER_TIME;
		double beat = quarters * 60.0 * sampleRate / (header.getTempo().getQuarterValue() * percent / 100.0);
		return Math.round(this.beats * beat);
	}

	// in ticks from the song start; Long.MAX_VALUE when it stops by itself
	long tick(TGSong song) {
		return (this.position != null ? this.position.tick(song) : Long.MAX_VALUE);
	}

	int passes() {
		return this.passes;
	}
}
