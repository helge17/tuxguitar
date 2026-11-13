package app.tuxguitar.audio;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.midi.synth.TGMidiProcessor;
import app.tuxguitar.midi.synth.TGMidiProcessorFactory;
import app.tuxguitar.midi.synth.TGSynthChannel;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.player.base.MidiTransport;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * An audio track's takes, live: the source of its channel on the TuxGuitar Synthesizer (TGSynthChannel.TAKES), so its
 * sound chain's effects follow it and its channel joins the output like any other. While the transport says the song
 * plays, it renders the track's tap (TGTakeTap: time-stretched, crossfaded, as offline) a block at a time on a
 * timeline of what plays from where playing started; when the transport is not where that timeline is (playing
 * started, moved, or the song jumped other than the timeline did) it starts a new timeline there. It delays the tap
 * itself by plugin delay compensation (it renders the timeline's samples that many frames back), so what else it
 * sounds can go undelayed.
 */
public final class TGLiveTakes implements TGMidiProcessor {

	// how far, in quarter notes, the transport may be from the timeline and still be on it
	private static final double ON_THE_TIMELINE = 1.0 / 64;

	private final TGContext context;
	private final MidiTransport expected = new MidiTransport();
	private float sampleRate = TGAudioBuffer.SAMPLE_RATE;
	private MidiTransport transport;
	private int channel;
	private int delay;
	private TGTimeline timeline;
	private TGTakeTap tap;
	// the timeline's sample the next block starts on
	private long position;

	TGLiveTakes(TGContext context) {
		this.context = context;
	}

	// audio tracks sound their takes on the TuxGuitar Synthesizer of the context
	public static void install(TGContext context) {
		TGSynthManager manager = TGSynthManager.getInstance(context);
		if (manager.findMidiProcessorFactory(TGSynthChannel.TAKES) == null) {
			manager.addExtension(TGMidiProcessorFactory.class, new TGMidiProcessorFactory() {
				public String getType() {
					return TGSynthChannel.TAKES;
				}

				public TGMidiProcessor createProcessor() {
					return new TGLiveTakes(context);
				}
			});
		}
	}

	public void setSampleRate(float sampleRate) {
		this.sampleRate = sampleRate;
	}

	public void setTransport(MidiTransport transport) {
		this.transport = transport;
	}

	public void restoreParameters(Map<String, String> parameters) {
		this.channel = Integer.parseInt(parameters.get(TGSynthChannel.TAKES_CHANNEL));
	}

	public boolean compensate(int frames) {
		this.delay = frames;
		return true;
	}

	public void process(float[][] block) {
		MidiTransport.Span span = this.transport.get(0);
		if (!span.isPlaying()) {
			this.tap = null;
			return;
		}
		if (this.tap == null || !this.isOnTheTimeline(span, block[0].length)) {
			this.start(span);
		}
		this.tap.render(block, this.position - this.delay);
		this.position += block[0].length;
	}

	private boolean isOnTheTimeline(MidiTransport.Span span, int frames) {
		this.timeline.tell(this.expected, this.position, frames);
		return Math.abs(this.expected.get(0).getQuarters() - span.getQuarters()) < ON_THE_TIMELINE;
	}

	// a timeline from the transport's position: to the end of the song, or for two passes of the Loop Range
	private void start(MidiTransport.Span span) {
		TGSong song = TGDocumentManager.getInstance(this.context).getSong();
		MidiPlayerMode mode = MidiPlayer.getInstance(this.context).getMode();
		long tick = Math.round(span.getQuarters() * TGDuration.QUARTER_TIME) + TGDuration.QUARTER_TIME;
		List<TGPlayedMeasure> played = TGSongTime.play(song, mode, tick, Long.MAX_VALUE, (mode.isLoop() ? 2 : 1));
		int rate = Math.round(this.sampleRate);
		this.timeline = new TGTimeline(song, played, rate);
		TGTrackSource[] sources = new TGTrackSource[played.size()];
		Arrays.fill(sources, TGTrackSource.TAKES);
		this.tap = TGTakeTap.of(this.track(song), this.timeline, new TGWsolaStretcher(rate), sources, null);
		this.position = 0;
	}

	// the audio track of the channel (an audio channel is its track's own)
	private TGTrack track(TGSong song) {
		Iterator<TGTrack> tracks = song.getTracks();
		TGTrack track = null;
		while (track == null) {
			TGTrack next = tracks.next();
			track = (next.getChannelId() == this.channel ? next : null);
		}
		return track;
	}

	public void sendNoteOn(int key, int velocity, int voice, boolean bendMode) {
	}

	public void sendNoteOff(int key, int velocity, int voice, boolean bendMode) {
	}

	public void sendPitchBend(int value, int voice, boolean bendMode) {
	}

	public void sendControlChange(int controller, int value) {
	}

	public void close() {
		this.tap = null;
	}

	public void finalize() {
	}

	public boolean isOpen() {
		return true;
	}

	public boolean isBusy() {
		return false;
	}

	public void fillBuffer(TGAudioBuffer buffer) {
	}

	public void storeParameters(Map<String, String> parameters) {
	}
}
