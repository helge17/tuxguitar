package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.models.TGTakeClip;
import app.tuxguitar.song.models.TGTrack;

/**
 * The takes of a track, read from the clips its measures hold: a take is on the track while one of its clips is.
 */
public final class TGTakes {

	private TGTakes() {
	}

	// newest first
	public static List<TGTakeSpan> list(TGTrack track) {
		Map<TGTake, TGTakeSpan> spans = new LinkedHashMap<TGTake, TGTakeSpan>();
		Iterator<TGMeasure> measures = track.getMeasures();
		while (measures.hasNext()) {
			TGMeasure measure = measures.next();
			for (TGTakeClip clip : measure.getTakeClips()) {
				spans.merge(clip.getTake(), span(measure, clip), TGTakes::cover);
			}
		}
		List<TGTakeSpan> list = new ArrayList<TGTakeSpan>(spans.values());
		list.sort(Comparator.comparingInt((TGTakeSpan span) -> span.getTake().getNumber()).reversed());
		return list;
	}

	private static TGTakeSpan span(TGMeasure measure, TGTakeClip clip) {
		return new TGTakeSpan(clip.getTake(), measure.getStart() + clip.getStart(), measure.getStart() + clip.getEnd());
	}

	private static TGTakeSpan cover(TGTakeSpan a, TGTakeSpan b) {
		return new TGTakeSpan(a.getTake(), Math.min(a.getStart(), b.getStart()), Math.max(a.getEnd(), b.getEnd()));
	}
}
