package app.tuxguitar.audio;

import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.player.base.MidiSampleClock;
import app.tuxguitar.song.models.TGSong;

/**
 * A double of the sequencer on the synthesizer's sample clock: once started, it tells the clock's transport what the
 * song plays from its first measure to its end, a block at a time, so many times back to back (the transport goes back
 * to the start each time); then nothing, so the song is stopped. It sends no event.
 */
final class TimelineSequencer implements MidiSampleClock.Listener {

	private final TGTimeline timeline;
	private final int times;
	// the frames told so far; none before it starts (started on another thread than the clock's)
	private volatile long position = -1;

	TimelineSequencer(TGSong song, int sampleRate, int times) {
		this.timeline = new TGTimeline(song, TGSongTime.play(song, new MidiPlayerMode(), song.getMeasureHeader(0).getStart(), Long.MAX_VALUE, 1), sampleRate);
		this.times = times;
	}

	// from the next block on
	void start() {
		this.position = 0;
	}

	// it has told all it plays
	boolean isDone() {
		return this.position >= this.times * this.timeline.length();
	}

	public void beforeBlock(MidiSampleClock clock, int frames) {
		if (this.position >= 0 && !this.isDone()) {
			this.timeline.tell(clock.getTransport(), this.position % this.timeline.length(), frames);
			this.position += frames;
		}
	}
}
