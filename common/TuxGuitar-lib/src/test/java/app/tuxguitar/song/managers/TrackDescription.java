package app.tuxguitar.song.managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;

/**
 * A track described one measure per line: "<number>: " then each beat as "<kind>@<position>/<duration>",
 * in position order; position in quarters from the measure start, duration "4" for a quarter, "2." for a dotted half;
 * kind = n<fret>[,<fret>...] (~ when tied, v<velocity> when not 95), r = rest, t = rest with a text, _ = empty voice.
 * Voice 0 only, unless asked: then voices are "|" separated.
 */
class TrackDescription {

	static final long QUARTER = TGDuration.WHOLE_PRECISE_DURATION / 4;

	static String describe(TGTrack track) {
		return describe(track, 1);
	}

	static String describe(TGTrack track, int voices) {
		StringBuilder description = new StringBuilder();
		for (int i = 0; i < track.countMeasures(); i++) {
			TGMeasure measure = track.getMeasure(i);
			List<TGBeat> beats = new ArrayList<TGBeat>(measure.getBeats());
			beats.sort(Comparator.comparingLong(TGBeat::getPreciseStart));
			description.append(measure.getNumber()).append(':');
			for (TGBeat beat : beats) {
				description.append(' ');
				for (int v = 0; v < voices; v++) {
					description.append(v == 0 ? "" : "|").append(describe(beat.getVoice(v), measure));
				}
			}
			description.append('\n');
		}
		return description.toString();
	}

	private static String describe(TGVoice voice, TGMeasure measure) {
		String kind = "_";
		if (!voice.isEmpty()) {
			kind = (voice.getBeat().isTextBeat() ? "t" : "r");
			for (TGNote note : voice.getNotes()) {
				kind = (kind.startsWith("n") ? kind + "," : "n") + note.getValue() + (note.isTiedNote() ? "~" : "") + (note.getVelocity() != 95 ? "v" + note.getVelocity() : "");
			}
		}
		double position = (voice.getBeat().getPreciseStart() - measure.getPreciseStart()) / (double) QUARTER;
		String formatted = (position == Math.rint(position)) ? String.valueOf((long) position) : String.valueOf(position);
		TGDuration duration = voice.getDuration();
		return kind + "@" + formatted + "/" + duration.getValue() + (duration.isDotted() ? "." : "");
	}
}
