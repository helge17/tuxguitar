package app.tuxguitar.audio.features;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.audio.TGPlayedMeasure;
import app.tuxguitar.audio.TGRender;
import app.tuxguitar.audio.TGTimeline;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;

/**
 * What playing or recording sounded, read as the features say it: a track's tap, the output, and where a position
 * sounds on the output's sample clock (which starts where playing started), in a pass from 0.
 */
final class Heard {

	private final TGRender render;
	private final TGSong song;

	Heard(TGRender render, TGSong song) {
		this.render = render;
		this.song = song;
	}

	float[] tap(TGTrack track) {
		return this.render.getTap(track);
	}

	// a side of the track's tap (0 left, 1 right)
	float[] tap(TGTrack track, int side) {
		return this.render.getTap(track, side);
	}

	// a side of the output (0 left, 1 right)
	float[] output(int side) {
		return (side == 0 ? this.left() : this.right());
	}

	// a side of the drum tap (0 left, 1 right)
	float[] drums(int side) {
		return this.render.getDrums(side);
	}

	float[] left() {
		return this.render.getLeft();
	}

	float[] right() {
		return this.render.getRight();
	}

	TGTimeline timeline() {
		return this.render.getTimeline();
	}

	// where the position sounds in the pass, the first time it does; the end of a measure where nothing follows it in
	// the pass is where that measure stops
	int sampleAt(SongPosition position, int pass) {
		long tick = position.tick(this.song);
		TGTimeline timeline = this.timeline();
		for (int i = 0; i < timeline.count(); i++) {
			TGPlayedMeasure measure = timeline.get(i);
			long start = measure.getHeader().getStart();
			if (measure.getPass() == pass && tick >= start + measure.getFrom() && tick < start + measure.getTo()) {
				return (int) timeline.sampleAt(i, tick - start);
			}
		}
		for (int i = timeline.count() - 1; i >= 0; i--) {
			TGPlayedMeasure measure = timeline.get(i);
			if (measure.getPass() == pass && tick == measure.getHeader().getStart() + measure.getTo()) {
				return (int) timeline.end(i);
			}
		}
		throw new IllegalArgumentException("not played: " + position);
	}

	// the plugin delay compensation: live, the output sounds the render this much later than the clock
	int latency() {
		return this.render.getLatency();
	}

	// the beats of the measures played, where they sound: each played measure's beats from where it starts playing
	List<Integer> beats() {
		TGTimeline timeline = this.timeline();
		List<Integer> beats = new ArrayList<Integer>();
		for (int i = 0; i < timeline.count(); i++) {
			TGPlayedMeasure measure = timeline.get(i);
			long beat = measure.getHeader().getTimeSignature().getDenominator().getTime();
			for (long tick = 0; tick < measure.getHeader().getLength(); tick += beat) {
				if (tick >= measure.getFrom() && tick < measure.getTo()) {
					beats.add((int) timeline.sampleAt(i, tick));
				}
			}
		}
		return beats;
	}

	// where the pass starts, and where the one before it ends
	int passStart(int pass) {
		TGTimeline timeline = this.timeline();
		for (int i = 0; i < timeline.count(); i++) {
			if (timeline.get(i).getPass() == pass) {
				return (int) timeline.start(i);
			}
		}
		throw new IllegalArgumentException("no pass " + pass);
	}

	// the samples where the measure plays in the first pass: {start, end}
	int[] measure(int number) {
		TGTimeline timeline = this.timeline();
		for (int i = 0; i < timeline.count(); i++) {
			if (timeline.get(i).getHeader().getNumber() == number) {
				return new int[] {(int) timeline.start(i), (int) timeline.end(i)};
			}
		}
		throw new IllegalArgumentException("measure " + number + " not played");
	}

	// where the tick of the measure sounds in the first pass
	int sampleIn(int number, long tick) {
		TGTimeline timeline = this.timeline();
		for (int i = 0; i < timeline.count(); i++) {
			if (timeline.get(i).getHeader().getNumber() == number) {
				return (int) timeline.sampleAt(i, tick);
			}
		}
		throw new IllegalArgumentException("measure " + number + " not played");
	}
}
