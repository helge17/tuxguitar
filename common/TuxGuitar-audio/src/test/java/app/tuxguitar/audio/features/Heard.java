package app.tuxguitar.audio.features;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

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

	// where the Metronome's clicks reach the drums
	long[] metronome() {
		return this.render.getMetronome().positions();
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
		List<Integer> beats = new ArrayList<Integer>();
		this.eachBeat((sample, tick) -> beats.add(sample.intValue()));
		return beats;
	}

	// the song tick of each beat played, in the order they play
	List<Long> beatTicks() {
		List<Long> ticks = new ArrayList<Long>();
		this.eachBeat((sample, tick) -> ticks.add(tick));
		return ticks;
	}

	// each beat played: where it sounds, its song tick
	private void eachBeat(BiConsumer<Long, Long> beat) {
		TGTimeline timeline = this.timeline();
		for (int i = 0; i < timeline.count(); i++) {
			beats(timeline, i, beat);
		}
	}

	// the beats of the played measure, from the first one at or after where it starts playing
	private static void beats(TGTimeline timeline, int index, BiConsumer<Long, Long> beat) {
		TGPlayedMeasure measure = timeline.get(index);
		long length = measure.getHeader().getTimeSignature().getDenominator().getTime();
		for (long tick = (measure.getFrom() + length - 1) / length * length; tick < measure.getTo(); tick += length) {
			beat.accept(timeline.sampleAt(index, tick), measure.getHeader().getStart() + tick);
		}
	}

	// where the player reports the song is after each block of 512 samples, live with the output latency
	long[] reported(int outputLatency) {
		return this.render.getReported(outputLatency);
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
