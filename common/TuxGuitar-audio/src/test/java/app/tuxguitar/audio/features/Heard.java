package app.tuxguitar.audio.features;

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
