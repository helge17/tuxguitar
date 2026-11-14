package app.tuxguitar.audio;

import app.tuxguitar.player.base.MidiSoundingPosition;
import app.tuxguitar.song.models.TGDuration;

/**
 * Where the player reports the song is (the played-beat highlight, the caret) while a timeline plays, as it reports it
 * live: after each block of the clock, the tick the output sounds then, the delay (plugin delay compensation and the
 * audio device's output latency) behind the clock. The timeline anchors a MidiSoundingPosition block by block, as the
 * live sequencer does, at the start of each played measure (where the tempo may change and the song may jump, as a
 * loop's jump back), in song ticks. The first measure is anchored from the start: before the output sounds it (the
 * Count In, the delay) the position is before where playing starts; with no measure played it is 0.
 */
public final class TGReportedPositions {

	private TGReportedPositions() {
	}

	// the tick reported at the end of each block of the timeline (the last one may run past its end)
	public static long[] of(TGTimeline timeline, int delay) {
		MidiSoundingPosition sounding = new MidiSoundingPosition();
		long[] reported = new long[(int) ((timeline.length() + TGBlocks.FRAMES - 1) / TGBlocks.FRAMES)];
		int next = 0;
		if (timeline.count() > 0) {
			anchor(sounding, timeline, next++);
		}
		for (int block = 0; block < reported.length; block++) {
			long end = (block + 1L) * TGBlocks.FRAMES;
			for (; next < timeline.count() && timeline.start(next) < end; next++) {
				anchor(sounding, timeline, next);
			}
			reported[block] = sounding.tickAt(end - delay);
		}
		return reported;
	}

	// from where the played measure starts, its first tick played, at its tempo
	private static void anchor(MidiSoundingPosition sounding, TGTimeline timeline, int index) {
		TGPlayedMeasure measure = timeline.get(index);
		sounding.anchor(timeline.start(index), measure.getHeader().getStart() + measure.getFrom(), measure.getTempo() * TGDuration.QUARTER_TIME, 60.0 * timeline.getSampleRate());
	}
}
