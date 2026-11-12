package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.helpers.TGTakePlayback;
import app.tuxguitar.song.models.TGTakeClip;
import app.tuxguitar.song.models.TGTakeSegment;
import app.tuxguitar.song.models.TGTrack;

/**
 * The takes of a track, read from and written to the clips its measures hold: a take is on the track while one of its
 * clips is. At each point a track plays the newest take there, unless a take was chosen for that measure (comping).
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

	// the take of that number on the track, or null
	public static TGTake find(TGTrack track, int number) {
		return list(track).stream().map(TGTakeSpan::getTake).filter(take -> take.getNumber() == number).findFirst().orElse(null);
	}

	// a new take over the played measures from first to last of the timeline, numbered from the track's next take
	// number; it plays where it was recorded. Each measure's clip starts at its sample of the take and keeps the tempo
	public static TGTake record(TGTrack track, float[] samples, TGTimeline timeline, int first, int last) {
		TGTake take = new TGTake(track.getNextTakeNumber(), samples, timeline.getSampleRate());
		track.setNextTakeNumber(take.getNumber() + 1);
		for (int i = first; i <= last; i++) {
			TGPlayedMeasure measure = timeline.get(i);
			TGMeasure trackMeasure = track.getMeasure(measure.getHeader().getNumber() - 1);
			trackMeasure.setChosenTake(null);
			int firstSample = (int) (timeline.start(i) - timeline.start(first));
			trackMeasure.addTakeClip(new TGTakeClip(take, measure.getFrom(), measure.getTo(), firstSample, measure.getTempo()));
		}
		return take;
	}

	// chooses the take for the measures it covers among them; false when it covers none
	public static boolean choose(TGTrack track, TGTake take, int firstMeasure, int lastMeasure) {
		boolean covers = false;
		for (int number = firstMeasure; number <= lastMeasure; number++) {
			TGMeasure measure = track.getMeasure(number - 1);
			if (measure != null && clips(measure).anyMatch(clip -> clip.getTake() == take)) {
				measure.setChosenTake(take);
				covers = true;
			}
		}
		return covers;
	}

	public static void delete(TGTrack track, TGTake take) {
		Iterator<TGMeasure> measures = track.getMeasures();
		while (measures.hasNext()) {
			measures.next().removeTake(take);
		}
	}

	// which take the measure plays, from where to where: one segment per change
	public static List<TGTakeSegment> playback(TGMeasure measure) {
		return TGTakePlayback.of(measure);
	}

	private static Stream<TGTakeClip> clips(TGMeasure measure) {
		return measure.getTakeClips().stream();
	}

}
