package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.helpers.TGTakePlayback;
import app.tuxguitar.song.models.TGTakeClip;
import app.tuxguitar.song.models.TGTrack;

/**
 * What an audio track sounds over a timeline, sample by sample: in each played measure its takes as TGTakes says
 * which plays where, its monitored input, or nothing. A take plays at the song's tempo: sample for sample at the tempo
 * it was recorded at, time-stretched otherwise. Where it goes from one take to another, the newer take's side
 * crossfades over 256 samples; at a jump in song time or where a take is cut, the audio before fades out; against
 * silence, nothing fades. The tap has two sides: a mono take or input sounds on both, a stereo one on its own.
 */
public final class TGTakeTap {

	private TGTakeTap() {
	}

	// sources: what the track sounds in each played measure; input: the monitored input's sides on the output clock,
	// as long as the timeline (or null when no measure plays it); the tap's left and right sides
	public static float[][] render(TGTrack track, TGTimeline timeline, TGTimeStretcher stretcher, TGTrackSource[] sources, float[][] input) {
		List<TGTakePiece> pieces = pieces(track, timeline, stretcher, sources, input);
		fade(pieces);
		float[][] tap = new float[2][(int) timeline.length()];
		for (TGTakePiece piece : pieces) {
			if (piece.sounds()) {
				piece.addTo(tap);
			}
		}
		return tap;
	}

	private static List<TGTakePiece> pieces(TGTrack track, TGTimeline timeline, TGTimeStretcher stretcher, TGTrackSource[] sources, float[][] input) {
		Map<TGTake, float[][]> samples = new HashMap<TGTake, float[][]>();
		List<TGTakePiece> pieces = new ArrayList<TGTakePiece>();
		for (int i = 0; i < timeline.count(); i++) {
			TGPlayedMeasure played = timeline.get(i);
			TGMeasure measure = track.getMeasure(played.getHeader().getNumber() - 1);
			if (sources[i] == TGTrackSource.TAKES) {
				pieces.addAll(new Measure(timeline, i, measure, stretcher, samples).pieces());
			} else {
				Map<TGTake, int[]> under = new Measure(timeline, i, measure, stretcher, samples).under(played.getFrom(), played.getTo());
				pieces.add(new TGTakePiece(timeline.start(i), timeline.end(i), played.isJump(), (sources[i] == TGTrackSource.INPUT ? input : null), under));
			}
		}
		return pieces;
	}

	// the crossfade rules, at each boundary between two pieces
	private static void fade(List<TGTakePiece> pieces) {
		for (int i = 1; i < pieces.size(); i++) {
			fade(pieces.get(i - 1), pieces.get(i));
		}
	}

	private static void fade(TGTakePiece before, TGTakePiece after) {
		if (!before.sounds() || before.playsOnInto(after)) {
			return;
		}
		if (after.jump || !after.sounds()) {
			before.fadeOut = (after.jump || before.hasMore());
		} else {
			crossfade(before, after);
		}
	}

	// from one take to another: the newer one's side crossfades when the older one goes on there; else a cut. Between
	// two different chosen takes the newer one fades on its side even where the older has no audio. Two pieces of the
	// same take that do not play on into each other (a cut inside it) are as going back down to an older take that
	// was not going on under it: the audio before fades out if the take goes on past it
	private static void crossfade(TGTakePiece before, TGTakePiece after) {
		boolean comp = (before.take != after.take && before.chosen && after.chosen);
		if (after.rank > before.rank) {
			boolean under = before.goesOnUnder(after);
			after.fadeIn = (under || comp);
			before.tail = under;
			before.fadeOut = (!under && before.hasMore());
		} else {
			boolean under = before.wasGoingOnUnder(after);
			before.fadeOut = (under || comp || before.hasMore());
			after.head = under;
		}
	}

	// a played measure of the track, cut into pieces where the take that plays changes
	private static final class Measure {
		private final TGTimeline timeline;
		private final int index;
		private final TGMeasure measure;
		private final TGPlayedMeasure played;
		private final TGTimeStretcher stretcher;
		private final Map<TGTake, float[][]> samples;

		Measure(TGTimeline timeline, int index, TGMeasure measure, TGTimeStretcher stretcher, Map<TGTake, float[][]> samples) {
			this.timeline = timeline;
			this.index = index;
			this.measure = measure;
			this.played = timeline.get(index);
			this.stretcher = stretcher;
			this.samples = samples;
		}

		// the clips that play, cut to the measure as played, silence between them
		List<TGTakePiece> pieces() {
			List<TGTakePiece> pieces = new ArrayList<TGTakePiece>();
			long tick = this.played.getFrom();
			for (TGTakeClip clip : TGTakePlayback.clips(this.measure)) {
				long from = Math.max(clip.getStart(), this.played.getFrom());
				long to = Math.min(clip.getEnd(), this.played.getTo());
				if (to > from) {
					this.silence(pieces, tick, from);
					pieces.add(this.piece(clip, from, to));
					tick = to;
				}
			}
			this.silence(pieces, tick, this.played.getTo());
			return pieces;
		}

		private void silence(List<TGTakePiece> pieces, long from, long to) {
			if (to > from) {
				pieces.add(new TGTakePiece(this.sampleAt(from), this.sampleAt(to), this.jumpsTo(from), null, this.under(from, to)));
			}
		}

		private long sampleAt(long tick) {
			return (tick == this.played.getTo() ? this.timeline.end(this.index) : this.timeline.sampleAt(this.index, tick));
		}

		private boolean jumpsTo(long tick) {
			return (tick == this.played.getFrom() && this.played.isJump());
		}

		private TGTakePiece piece(TGTakeClip clip, long from, long to) {
			long start = this.sampleAt(from);
			long end = this.sampleAt(to);
			TGTake take = clip.getTake();
			int takeStart = this.sampleOf(clip, from);
			int takeEnd = this.sampleOf(clip, to);
			float[][] source = this.samples.computeIfAbsent(clip.getSource(), TGTakeTap::samplesOf);
			float[][] audio = (clip.getTempo() == this.played.getTempo() ? null : this.stretch(source, takeStart, takeEnd, (int) (end - start)));
			boolean chosen = (this.measure.getChosenTake() == take);
			return new TGTakePiece(start, end, this.jumpsTo(from), take, chosen, takeStart, takeEnd, source, audio, this.under(from, to));
		}

		private float[][] stretch(float[][] source, int from, int to, int length) {
			int first = Math.max(0, Math.min(from, source[0].length));
			int last = Math.max(first, Math.min(to, source[0].length));
			return (last > first ? this.stretcher.stretch(source, first, last, length) : new float[source.length][length]);
		}

		// the takes covering the ticks, with the samples of each that would play there
		Map<TGTake, int[]> under(long from, long to) {
			Map<TGTake, int[]> under = new HashMap<TGTake, int[]>();
			for (TGTakeClip clip : this.measure.getTakeClips()) {
				if (clip.getStart() <= from && from < clip.getEnd()) {
					under.put(clip.getTake(), new int[] {this.sampleOf(clip, from), this.sampleOf(clip, to)});
				}
			}
			return under;
		}

		// the sample of the take at the tick: counted on the output clock at the tempo it was recorded at, so it plays
		// sample for sample; counted at its recording tempo otherwise
		private int sampleOf(TGTakeClip clip, long tick) {
			if (clip.getTempo() == this.played.getTempo()) {
				return (int) (clip.getFirstSample() + this.timeline.sampleAt(this.index, tick) - this.timeline.sampleAt(this.index, clip.getStart()));
			}
			return clip.sampleAt(tick);
		}
	}

	// one array per side of the take
	private static float[][] samplesOf(TGTake take) {
		float[][] samples = new float[take.countChannels()][take.countSamples()];
		for (int side = 0; side < samples.length; side++) {
			for (int i = 0; i < samples[side].length; i++) {
				samples[side][i] = take.getSample(side, i);
			}
		}
		return samples;
	}
}
