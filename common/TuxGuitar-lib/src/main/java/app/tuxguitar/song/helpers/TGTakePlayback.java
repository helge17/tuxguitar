package app.tuxguitar.song.helpers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.models.TGTakeClip;
import app.tuxguitar.song.models.TGTakeSegment;

/**
 * What an audio track plays in a measure: at each point the newest take there, unless a take was chosen for the
 * measure (comping) and covers that point. Read as segments, one per change. Playing, recording and pasting audio
 * all follow it.
 */
public final class TGTakePlayback {

	private TGTakePlayback() {
	}

	public static List<TGTakeSegment> of(TGMeasure measure) {
		List<TGTakeSegment> segments = new ArrayList<TGTakeSegment>();
		for (long tick : boundaries(measure)) {
			TGTake take = playing(measure, tick);
			if (segments.isEmpty() || segments.get(segments.size() - 1).getTake() != take) {
				segments.add(new TGTakeSegment(tick, take));
			}
		}
		return segments;
	}

	// the clips that play in the measure, in order, each cut where another take takes over
	public static List<TGTakeClip> clips(TGMeasure measure) {
		List<TGTakeClip> clips = new ArrayList<TGTakeClip>();
		List<TGTakeSegment> segments = of(measure);
		for (int k = 0; k < segments.size(); k++) {
			long end = (k + 1 < segments.size() ? segments.get(k + 1).getStart() : measure.getLength());
			TGTake take = segments.get(k).getTake();
			for (long tick = segments.get(k).getStart(); take != null && tick < end; ) {
				TGTakeClip clip = clipAt(measure, take, tick);
				long to = Math.min(clip.getEnd(), end);
				clips.add(clip.copy(take, tick, to));
				tick = to;
			}
		}
		return clips;
	}

	// the clip of the take that plays at the tick
	private static TGTakeClip clipAt(TGMeasure measure, TGTake take, long tick) {
		return clipsAt(measure, tick).filter(clip -> clip.getTake() == take).findFirst().get();
	}

	// the measure's start, and every start or end of a clip inside it
	private static SortedSet<Long> boundaries(TGMeasure measure) {
		SortedSet<Long> ticks = new TreeSet<Long>();
		ticks.add(0L);
		for (TGTakeClip clip : measure.getTakeClips()) {
			ticks.add(clip.getStart());
			ticks.add(clip.getEnd());
		}
		return ticks.headSet(measure.getLength());
	}

	// the chosen take where it covers the tick, else the newest take there
	private static TGTake playing(TGMeasure measure, long tick) {
		TGTake chosen = measure.getChosenTake();
		if (clipsAt(measure, tick).anyMatch(clip -> clip.getTake() == chosen)) {
			return chosen;
		}
		return clipsAt(measure, tick).map(TGTakeClip::getTake).max(Comparator.comparingInt(TGTake::getNumber)).orElse(null);
	}

	private static Stream<TGTakeClip> clipsAt(TGMeasure measure, long tick) {
		return measure.getTakeClips().stream().filter(clip -> clip.getStart() <= tick && tick < clip.getEnd());
	}
}
