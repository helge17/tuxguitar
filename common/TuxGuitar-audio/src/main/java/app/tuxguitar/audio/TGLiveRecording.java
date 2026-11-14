package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiTransport;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * A recording live, on the live audio device. From its start it keeps every frame the device's input channels deliver
 * (all of them, not the device's last 30 seconds), and follows a block at a time what the song plays: each stretch the
 * transport plays on one timeline (TGLiveTimeline, one pass a timeline) is a part, from the frame of the device's clock
 * it starts on, with the plugin delay compensation of what is heard then. A Count In (a timer before the sequencer
 * plays) plays no part. The recording ends once the player has stopped and the device has delivered one round trip
 * past the last frame played; then TGRecording records each part's timeline, cut where it stopped playing, on the
 * input kept as seen from the timeline's clock, so its takes are placed as offline: by the round trip, one per stretch
 * without a jump (so one per pass of a loop), inside the punch range.
 */
public final class TGLiveRecording implements TGLiveDevice.Listener {

	// how often the end of the recording is looked for
	private static final long LOOK = TimeUnit.MILLISECONDS.toNanos(5);
	// how long the device may deliver nothing before its stream is taken as ended: a block lasts some milliseconds
	private static final long STALL = TimeUnit.SECONDS.toNanos(1);

	private final TGContext context;
	private final TGLiveDevice device;
	private final TGLiveTimeline followed;
	// a block of every input channel at a time, from the first frame kept on; the device's thread adds to it
	private final List<float[][]> kept = Collections.synchronizedList(new ArrayList<float[][]>());
	private final List<Part> parts = Collections.synchronizedList(new ArrayList<Part>());
	private long first = -1;
	// written by the device's thread after each block: the frames delivered, whether the block played, and where the
	// input must have been delivered to for the parts played so far (one round trip past the last frame played)
	private volatile long delivered;
	private volatile boolean playing;
	private volatile long until;

	private TGLiveRecording(TGContext context, TGLiveDevice device) {
		this.context = context;
		this.device = device;
		this.followed = new TGLiveTimeline(context, 1, false);
		this.followed.setSampleRate(device.getSampleRate());
	}

	// from the device's next block on: the player is to play once it has started
	public static TGLiveRecording start(TGContext context, TGLiveDevice device) {
		TGLiveRecording recording = new TGLiveRecording(context, device);
		device.addListener(recording);
		return recording;
	}

	// on the device's thread
	public void played(long clock, int frames) {
		this.keep(clock, frames);
		MidiTransport transport = this.device.getTransport();
		boolean playing = transport.get(0).isPlaying();
		for (int span = 0; playing && span < transport.count(); span++) {
			int offset = transport.get(span).getFrame();
			this.follow(transport.get(span), clock + offset, TGLiveTimeline.end(transport, span, frames) - offset, this.device.getSynthesizer().getLatency());
		}
		if (!playing) {
			this.followed.stop();
		}
		this.playing = playing;
		this.delivered = clock + frames;
	}

	private void keep(long clock, int frames) {
		this.first = (this.first < 0 ? clock : this.first);
		float[][] block = new float[this.device.getInputChannels()][];
		for (int c = 0; c < block.length; c++) {
			block[c] = this.device.capture(c + 1, clock, frames);
		}
		this.kept.add(block);
	}

	private void follow(MidiTransport.Span span, long clock, int frames, int compensation) {
		if (this.followed.follow(span, frames)) {
			this.parts.add(new Part(this.followed.getTimeline(), clock, compensation));
		}
		Part part = this.parts.get(this.parts.size() - 1);
		part.frames += frames;
		this.until = clock + frames + TGAudioSession.getInstance(this.context).getRoundTrip(part.compensation);
	}

	// waits until the player has stopped and the device has delivered one round trip past what played (or the thread
	// is interrupted), then records the parts on the tracks; how many takes they made. Should the device deliver nothing
	// for STALL meanwhile (its stream ended), the recording ends there and stops the player.
	public int end(TGSongManager songManager, TGSong song, List<TGTrack> tracks) {
		MidiPlayer player = MidiPlayer.getInstance(this.context);
		if (this.stalls(player)) {
			player.stop();
		}
		this.device.removeListener(this);
		TGAudioSession session = TGAudioSession.getInstance(this.context);
		int takes = 0;
		for (Part part : new ArrayList<Part>(this.parts)) {
			TGTimeline timeline = part.timeline.until(part.frames);
			takes += new TGRecording(songManager, song, session, new Kept(part.start - timeline.getClock()), part.compensation).takes(tracks, timeline);
		}
		return takes;
	}

	// waits until the recording is over or the thread is interrupted: false; or until the device has delivered nothing
	// for STALL: true
	private boolean stalls(MidiPlayer player) {
		long seen = this.delivered;
		long since = System.nanoTime();
		while (!this.isOver(player) && !Thread.currentThread().isInterrupted()) {
			if (this.delivered != seen) {
				seen = this.delivered;
				since = System.nanoTime();
			} else if (System.nanoTime() - since >= STALL) {
				return true;
			}
			LockSupport.parkNanos(LOOK);
		}
		return false;
	}

	private boolean isOver(MidiPlayer player) {
		return !player.isRunning() && !this.playing && this.delivered >= this.until;
	}

	// a stretch played on one timeline
	private static final class Part {
		private final TGTimeline timeline;
		// the device's frame it starts on
		private final long start;
		private final int compensation;
		private long frames;

		Part(TGTimeline timeline, long start, int compensation) {
			this.timeline = timeline;
			this.start = start;
			this.compensation = compensation;
		}
	}

	// the input kept, on a clock the offset before the device's
	private final class Kept implements TGAudioDevice {
		private final long offset;

		Kept(long offset) {
			this.offset = offset;
		}

		public int getSampleRate() {
			return TGLiveRecording.this.device.getSampleRate();
		}

		public float[] capture(int inputChannel, long from, int count) {
			float[] samples = new float[count];
			for (int i = 0; i < count; i++) {
				samples[i] = TGLiveRecording.this.sample(inputChannel - 1, from + this.offset + i);
			}
			return samples;
		}
	}

	// the input channel's sample at the device's frame; silence where none was kept
	private float sample(int channel, long frame) {
		long index = frame - this.first;
		int block = (int) (index / TGBlocks.FRAMES);
		return (index >= 0 && block < this.kept.size() ? this.kept.get(block)[channel][(int) (index % TGBlocks.FRAMES)] : 0f);
	}
}
