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
 * What the song plays live, followed a block at a time from the transport: a timeline of what plays from where the
 * transport was when playing started, and the sample of it each block starts on. When the transport is not where the
 * timeline is (playing started, moved, or the song jumped other than the timeline did) a new timeline starts there:
 * to the end of the song, or over as many passes of the Loop Range as asked for when Play looped is on.
 */
final class TGLiveTimeline {

	// how far, in quarter notes, the transport may be from the timeline and still be on it
	private static final double ON_THE_TIMELINE = 1.0 / 64;

	private final TGContext context;
	private final int passes;
	private final MidiTransport expected = new MidiTransport();
	private int sampleRate = Math.round(TGAudioBuffer.SAMPLE_RATE);
	private TGTimeline timeline;
	// the timeline's sample the block followed last starts on, and its frames
	private long position;
	private int frames;

	TGLiveTimeline(TGContext context, int passes) {
		this.context = context;
		this.passes = passes;
	}

	void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	// the next block, which the transport says plays from the span on: whether a new timeline starts with it
	boolean follow(MidiTransport.Span span, int frames) {
		this.position += this.frames;
		this.frames = frames;
		if (this.timeline != null && this.isOnTheTimeline(span)) {
			return false;
		}
		this.start(span);
		return true;
	}

	// the song stopped: playing again starts a new timeline
	void stop() {
		this.timeline = null;
	}

	TGTimeline getTimeline() {
		return this.timeline;
	}

	// the timeline's sample the block followed last starts on
	long getPosition() {
		return this.position;
	}

	private boolean isOnTheTimeline(MidiTransport.Span span) {
		this.timeline.tell(this.expected, this.position, this.frames);
		return Math.abs(this.expected.get(0).getQuarters() - span.getQuarters()) < ON_THE_TIMELINE;
	}

	private void start(MidiTransport.Span span) {
		TGSong song = TGDocumentManager.getInstance(this.context).getSong();
		MidiPlayerMode mode = MidiPlayer.getInstance(this.context).getMode();
		long tick = Math.round(span.getQuarters() * TGDuration.QUARTER_TIME) + TGDuration.QUARTER_TIME;
		List<TGPlayedMeasure> played = TGSongTime.play(song, mode, tick, Long.MAX_VALUE, (mode.isLoop() ? this.passes : 1));
		this.timeline = new TGTimeline(song, played, this.sampleRate);
		this.position = 0;
	}
}
