package app.tuxguitar.audio.features;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;

/**
 * A position as the features write it: "measure 2 beat 3" (beat 2.5 is half way through beat 2, a beat being the unit of
 * the measure's time signature), or "the end of measure 4", where measure 5 would start.
 */
final class SongPosition {

	static final String PATTERN = "measure \\d+ beat [\\d.]+|the end of measure \\d+";

	private static final Pattern BEAT = Pattern.compile("measure (\\d+) beat ([\\d.]+)");
	private static final Pattern END = Pattern.compile("the end of measure (\\d+)");

	private final int measure;
	private final BigDecimal beat;

	private SongPosition(int measure, BigDecimal beat) {
		this.measure = measure;
		this.beat = beat;
	}

	static SongPosition parse(String text) {
		Matcher end = END.matcher(text);
		if (end.matches()) {
			return new SongPosition(Integer.parseInt(end.group(1)) + 1, BigDecimal.ONE);
		}
		Matcher beat = BEAT.matcher(text);
		beat.matches();
		return new SongPosition(Integer.parseInt(beat.group(1)), new BigDecimal(beat.group(2)));
	}

	int measure() {
		return this.measure;
	}

	// in ticks from the song start; past the last measure, beat 1 of the next one is where the song ends
	long tick(TGSong song) {
		if (this.measure > song.countMeasureHeaders()) {
			TGMeasureHeader last = song.getMeasureHeader(song.countMeasureHeaders() - 1);
			return last.getStart() + last.getLength();
		}
		TGMeasureHeader header = song.getMeasureHeader(this.measure - 1);
		long beatLength = header.getTimeSignature().getDenominator().getTime();
		return header.getStart() + this.beat.subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(beatLength)).longValueExact();
	}
}
