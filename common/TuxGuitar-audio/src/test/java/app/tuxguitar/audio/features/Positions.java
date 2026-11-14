package app.tuxguitar.audio.features;

import java.math.BigDecimal;

import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;

/**
 * Positions in the song, in ticks (quarter-note time from the song start), read as the features write them: "m2b3" for
 * measure 2 beat 3, "m2b2.5" half way through beat 2. A beat is the unit of the measure's time signature. Where the
 * song ends reads as the first beat of the measure after the last.
 */
final class Positions {

	private Positions() {
	}

	static String read(TGSong song, long tick) {
		TGMeasureHeader last = song.getMeasureHeader(song.countMeasureHeaders() - 1);
		if (tick >= last.getStart() + last.getLength()) {
			return "m" + (last.getNumber() + 1) + "b1";
		}
		TGMeasureHeader header = headerAt(song, tick);
		BigDecimal beats = BigDecimal.valueOf(tick - header.getStart()).divide(BigDecimal.valueOf(beatLength(header)));
		return "m" + header.getNumber() + "b" + beats.add(BigDecimal.ONE).stripTrailingZeros().toPlainString();
	}

	private static TGMeasureHeader headerAt(TGSong song, long tick) {
		int index = 0;
		while (song.getMeasureHeader(index).getStart() + song.getMeasureHeader(index).getLength() <= tick) {
			index++;
		}
		return song.getMeasureHeader(index);
	}

	private static long beatLength(TGMeasureHeader header) {
		return header.getTimeSignature().getDenominator().getTime();
	}
}
