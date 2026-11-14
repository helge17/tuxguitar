package app.tuxguitar.audio;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.midi.synth.TGMidiProcessor;
import app.tuxguitar.midi.synth.TGMidiProcessorFactory;
import app.tuxguitar.midi.synth.TGSynthChannel;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.player.base.MidiTransport;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * An audio track's takes, live: the source of its channel on the TuxGuitar Synthesizer (TGSynthChannel.TAKES), so its
 * sound chain's effects follow it and its channel joins the output like any other. While the transport says the song
 * plays, it renders the track's tap (TGTakeTap: time-stretched, crossfaded, as offline) a block at a time on the
 * timeline of what plays (TGLiveTimeline: two passes of the Loop Range when looped, after the pass that ended). It
 * delays the tap itself by plugin delay compensation (it renders the timeline's samples that many frames back, so after
 * a jump back the end of the pass that ended), so what else it sounds can go undelayed. A worker of its own makes the
 * stretches ahead of the blocks (TGStretchAhead).
 */
public final class TGLiveTakes implements TGMidiProcessor {

	private final TGContext context;
	private final TGLiveTimeline followed;
	private final ExecutorService worker = Executors.newSingleThreadExecutor(TGLiveTakes::daemon);
	private final TGStretchAhead ahead = new TGStretchAhead(this.worker);
	// the monitored input of the block being rendered
	private final TGTapInput input = new TGTapInput(0, null);
	private float sampleRate = TGAudioBuffer.SAMPLE_RATE;
	private MidiTransport transport;
	private int channel;
	private int delay;
	private TGTakeTap tap;

	TGLiveTakes(TGContext context) {
		this.context = context;
		this.followed = new TGLiveTimeline(context, 2, true);
	}

	// the worker's thread does not keep the app running
	private static Thread daemon(Runnable task) {
		Thread thread = new Thread(task, "TuxGuitar audio stretch ahead");
		thread.setDaemon(true);
		return thread;
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
		TGSong song = TGDocumentManager.getInstance(this.context).getSong();
		TGTrack track = this.track(song);
		TGAudioSession session = TGAudioSession.getInstance(this.context);
		boolean monitored = (TGTrackSource.stopped(session.isArmed(track), session.getMonitoring(track)) == TGTrackSource.INPUT);
		if (!this.transport.get(0).isPlaying()) {
			this.followed.stop();
			this.monitor(block, session, song, track, monitored);
			return;
		}
		for (int span = 0; span < this.transport.count(); span++) {
			int offset = this.transport.get(span).getFrame();
			this.play(block, offset, TGLiveTimeline.end(this.transport, span, block[0].length) - offset, this.transport.get(span), session, song, track, monitored);
		}
	}

	// the frames of the block from the offset, which the transport says play from the span on: on the timeline followed,
	// a new one starting there (a jump: a loop's next pass from the very frame it starts on)
	private void play(float[][] block, int offset, int frames, MidiTransport.Span span, TGAudioSession session, TGSong song, TGTrack track, boolean monitored) {
		if (this.followed.follow(span, frames)) {
			this.start(session, song, track);
		}
		long from = this.followed.getPosition() - this.delay;
		this.deliver(session, song, track, from - offset, block[0].length, monitored);
		this.tap.render(block, offset, frames, from);
		this.ahead.after(this.tap, from + frames, Math.round(this.sampleRate));
	}

	// while stopped, a monitored input sounds as it is delivered
	private void monitor(float[][] block, TGAudioSession session, TGSong song, TGTrack track, boolean monitored) {
		this.deliver(session, song, track, 0, block[0].length, monitored);
		for (int side = 0; monitored && side < block.length; side++) {
			for (int i = 0; i < block[side].length; i++) {
				block[side][i] += this.input.at(side, i);
			}
		}
	}

	// the input the device delivered for the block (the input the session's device is playing), from the tap's sample
	// on: undelayed, the input sounds as soon as its own chain lets it
	private void deliver(TGAudioSession session, TGSong song, TGTrack track, long from, int frames, boolean monitored) {
		if (monitored) {
			TGAudioDevice device = session.getDevice();
			TGAudioInput input = TGDocumentManager.getInstance(this.context).getSongManager().getChannel(song, track.getChannelId()).getAudioInput();
			this.input.set(from, device.capture(input, device.getClock(), frames));
		}
	}

	// the track's tap on the timeline just started: what it sounds in each measure, as offline
	private void start(TGAudioSession session, TGSong song, TGTrack track) {
		TGTimeline timeline = this.followed.getTimeline();
		TGTrackSource[] sources = TGTrackSource.of(session, song, track, timeline, session.isRecording(), session.isArmed(track));
		this.tap = TGTakeTap.of(track, timeline, new TGWsolaStretcher(Math.round(this.sampleRate)), sources, this.input);
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
		this.worker.shutdownNow();
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
