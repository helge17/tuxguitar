package app.tuxguitar.audio;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.midi.synth.TGMidiProcessor;
import app.tuxguitar.midi.synth.TGMidiProcessorFactory;
import app.tuxguitar.midi.synth.TGSynthChannel;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.player.base.MidiTransport;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * An audio track's takes, live: the source of its channel on the TuxGuitar Synthesizer (TGSynthChannel.TAKES), so its
 * sound chain's effects follow it and its channel joins the output like any other. While the transport says the song
 * plays, it renders the track's tap (TGTakeTap: time-stretched, crossfaded, as offline) a block at a time on the
 * timeline of what plays (TGLiveTimeline: two passes of the Loop Range when looped). It delays the tap itself by
 * plugin delay compensation (it renders the timeline's samples that many frames back), so what else it sounds can go
 * undelayed.
 */
public final class TGLiveTakes implements TGMidiProcessor {

	private final TGContext context;
	private final TGLiveTimeline followed;
	private float sampleRate = TGAudioBuffer.SAMPLE_RATE;
	private MidiTransport transport;
	private int channel;
	private int delay;
	private TGTakeTap tap;

	TGLiveTakes(TGContext context) {
		this.context = context;
		this.followed = new TGLiveTimeline(context, 2);
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
		this.followed.setSampleRate(Math.round(sampleRate));
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
			this.followed.stop();
			return;
		}
		if (this.followed.follow(span, block[0].length)) {
			this.start();
		}
		this.tap.render(block, this.followed.getPosition() - this.delay);
	}

	private void start() {
		TGSong song = TGDocumentManager.getInstance(this.context).getSong();
		TGTimeline timeline = this.followed.getTimeline();
		TGTrackSource[] sources = new TGTrackSource[timeline.count()];
		Arrays.fill(sources, TGTrackSource.TAKES);
		this.tap = TGTakeTap.of(this.track(song), timeline, new TGWsolaStretcher(Math.round(this.sampleRate)), sources, null);
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
		this.followed.stop();
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
