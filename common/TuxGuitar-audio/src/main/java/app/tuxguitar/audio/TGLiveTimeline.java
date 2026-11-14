package app.tuxguitar.audio;

import java.util.List;

import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.player.base.MidiTransport;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

/**
 * What the song plays live, followed a stretch at a time from the transport (each span it was told for a block, over
 * the frames the span lasts): a timeline of what plays from where the transport was when it started, and the sample of
 * it each stretch starts on. A new timeline starts when playing starts, when the transport is not where the timeline
 * is (it moved, or the song jumped other than the timeline did), and when it goes back (a loop's next pass, which plays
 * as the mode's next loop: so the timeline, over as many passes of the Loop Range as asked for when Play looped is on,
 * always foresees the next jump back): to the end of the song, or over those passes.
 */
final class TGLiveTimeline {

	// how far, in quarter notes, the transport may be from the timeline and still be on it
	private static final double ON_THE_TIMELINE = 1.0 / 64;

	private final TGContext context;
	private final int passes;
	private final MidiTransport expected = new MidiTransport();
	private int sampleRate = Math.round(TGAudioBuffer.SAMPLE_RATE);
	private TGTimeline timeline;
	// the mode the timeline plays (a copy of the player's when playing started or moved; its next loop for a next pass)
	private final MidiPlayerMode mode = new MidiPlayerMode();
	// the timeline's sample the stretch followed last starts on, its frames, and the quarter note it starts on
	private long position;
	private int frames;
	private double quarters;

	TGLiveTimeline(TGContext context, int passes) {
		this.context = context;
		this.passes = passes;
	}

	void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	// the frame of the block the transport's span ends on: the next span's, else the block's end
	static int end(MidiTransport transport, int span, int frames) {
		return (span + 1 < transport.count() ? transport.get(span + 1).getFrame() : frames);
	}

	// the next stretch, which the transport says plays from the span on, over the frames: whether a new timeline starts
	// with it
	boolean follow(MidiTransport.Span span, int frames) {
		this.position += this.frames;
		this.frames = frames;
		boolean back = (this.timeline != null && span.getQuarters() < this.quarters);
		this.quarters = span.getQuarters();
		if (this.timeline != null && !back && this.isOnTheTimeline(span)) {
			return false;
		}
		this.start(span, back);
		return true;
	}

	// the song stopped: playing again starts a new timeline
	void stop() {
		this.timeline = null;
	}

	TGTimeline getTimeline() {
		return this.timeline;
	}

	// the timeline's sample the stretch followed last starts on
	long getPosition() {
		return this.position;
	}

	private boolean isOnTheTimeline(MidiTransport.Span span) {
		this.timeline.tell(this.expected, this.position, this.frames);
		return Math.abs(this.expected.get(0).getQuarters() - span.getQuarters()) < ON_THE_TIMELINE;
	}

	private void start(MidiTransport.Span span, boolean nextPass) {
		if (nextPass) {
			this.mode.notifyLoop();
		} else {
			this.mode.copyFrom(MidiPlayer.getInstance(this.context).getMode());
		}
		TGSong song = TGDocumentManager.getInstance(this.context).getSong();
		long tick = Math.round(span.getQuarters() * TGDuration.QUARTER_TIME) + TGDuration.QUARTER_TIME;
		List<TGPlayedMeasure> played = TGSongTime.play(song, this.mode, tick, Long.MAX_VALUE, (this.mode.isLoop() ? this.passes : 1));
		this.timeline = new TGTimeline(song, played, this.sampleRate);
		this.position = 0;
	}
}
