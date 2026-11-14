package app.tuxguitar.player.impl.sequencer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiSampleClock;
import app.tuxguitar.player.base.MidiSoundingPosition;
import app.tuxguitar.player.base.MidiTransport;
import app.tuxguitar.song.models.TGDuration;

/**
 * Plays the sequence on a sample clock, a block of frames at a time: each event whose tick sounds in the block goes to
 * the clock's receiver of its frame. A tick sounds on the frame its time from the anchor reaches at the tempo, rounded
 * to the nearest frame. Playing from a position anchors it on the first frame of the next block, and sends again the
 * channel setup before it there; a tempo event moves the anchor to its own tick and frame. A time signature is sent to
 * no receiver. The clock's transport is told each tempo and time signature from its frame on.
 *
 * The sequencer's position is the tick the clock's output sounds at the end of the block: the anchors (where playing
 * starts or moves, and each tempo) are kept by frame of the clock, and the output sounds a frame the clock's sounding
 * delay after it. Until the output sounds where playing started, the position is before it; the next pass of a loop
 * keeps the anchors, so until the output sounds it the position is where the output still sounds the last pass.
 */
class MidiBlockPlayer {

	private static final double MICROSECONDS_PER_QUARTER = 1000000.0 * TGDuration.QUARTER_TIME;

	private final MidiSequencerImpl sequencer;
	// put in tick order when playing starts or moves, events on the same tick in the order they were added
	private final List<MidiEvent> events = new ArrayList<MidiEvent>();
	private boolean moved;
	// the first event not sent yet
	private int next;
	private long anchorTick;
	// the anchor's frame, counted from the first frame of the next block
	private long anchorFrame;
	private int usq;
	// the last time signature reached; none since playing moved: the one told before is kept
	private MidiEvent signature;
	// told when the clock has no transport
	private final MidiTransport untold = new MidiTransport();
	// the anchors by frame of the clock, counted from the first block played
	private final MidiSoundingPosition sounding = new MidiSoundingPosition();
	// the frames played before the next block
	private long played;
	// the tick at the end of the last block played
	private volatile long scheduled;
	// where the sequence plays again once its end is reached (negative: nowhere), and how many times it did
	private long loopStart = -1;
	private int passes;
	// the events of the next pass, when given: from where it starts they replace the events
	private final List<MidiEvent> nextPass = new ArrayList<MidiEvent>();

	MidiBlockPlayer(MidiSequencerImpl sequencer) {
		this.sequencer = sequencer;
	}

	void addEvent(MidiEvent event) {
		this.events.add(event);
	}

	void clearEvents() {
		this.events.clear();
	}

	void setTempoInUSQ(int usq) {
		this.usq = usq;
	}

	// playing goes on anew from the sequencer's position, with the next block
	void reset() {
		this.moved = true;
	}

	long getScheduledTick() {
		return this.scheduled;
	}

	// from now on the sequence plays again from the tick each time its end is reached (a negative tick: it ends there)
	void setLoopStart(long tick) {
		this.loopStart = tick;
	}

	// how many times the sequence played again
	int getPasses() {
		return this.passes;
	}

	// the next pass's events are given again
	void clearNextPass() {
		this.nextPass.clear();
	}

	void addNextPassEvent(MidiEvent event) {
		this.nextPass.add(event);
	}

	// sends the events of the next block and moves the sequencer's position to the tick the output sounds at its end; the
	// end of a loop reached in the block starts the next pass on its frame
	void process(MidiSampleClock clock, int frames) throws MidiPlayerException {
		float rate = clock.getSampleRate();
		if (this.moved) {
			this.move(clock, this.sequencer.getTickPosition());
		}
		MidiTransport transport = (clock.getTransport() != null ? clock.getTransport() : this.untold);
		this.tell(transport, rate);
		this.playBefore(frames, clock, transport);
		while (this.loopsHere()) {
			this.wrap(clock, transport, this.frameOf(this.sequencer.getTickLength(), rate));
			this.playBefore(frames, clock, transport);
		}
		this.played += frames;
		this.anchorFrame -= frames;
		this.scheduled = this.sounding.tickAt(this.played);
		this.sequencer.getMidiTickPlayer().setTick(this.sounding.tickAt(this.played - clock.getSoundingDelay()));
	}

	// the events not sent yet whose frames are in the block
	private void playBefore(int frames, MidiSampleClock clock, MidiTransport transport) throws MidiPlayerException {
		for (; this.next < this.events.size(); this.next++) {
			MidiEvent event = this.events.get(this.next);
			long frame = this.frameOf(event.getTick(), clock.getSampleRate());
			if (frame >= frames) {
				break;
			}
			this.play(event, frame, clock, transport);
		}
	}

	// a loop whose end (the sequence's last tick, after its start) is reached: every event of the pass is sent
	private boolean loopsHere() {
		return (this.loopStart >= 0 && this.loopStart < this.sequencer.getTickLength() && this.next == this.events.size());
	}

	// the next pass from the frame the end sounds on, from the loop's start, with the next pass's events when given: the
	// anchors are kept (until the output sounds the next pass the position is this one's), the transport told from there
	private void wrap(MidiSampleClock clock, MidiTransport transport, long frame) throws MidiPlayerException {
		this.passes++;
		if (!this.nextPass.isEmpty()) {
			this.events.clear();
			this.events.addAll(this.nextPass);
			this.events.sort(Comparator.comparingLong(MidiEvent::getTick));
			this.nextPass.clear();
		}
		this.seek(clock, this.loopStart, frame);
		this.sounding.anchor(this.played + frame, this.loopStart, MICROSECONDS_PER_QUARTER, this.usq * (double) clock.getSampleRate());
		this.span(transport, frame, clock.getSampleRate());
	}

	// an event to the receiver of its frame; a tempo (sent, so the player takes it on) moves the anchor there; it, or a
	// time signature, also starts a span of the transport there
	private void play(MidiEvent event, long frame, MidiSampleClock clock, MidiTransport transport) throws MidiPlayerException {
		if (event.isTimeSignature()) {
			this.signature = event;
		} else if (this.sequencer.getMidiTrackController().isAudible(event.getTrack())) {
			this.sequencer.sendEvent(event, clock.at((int) frame));
		}
		if (event.isTempo()) {
			this.anchor(event.getTick(), frame, clock.getSampleRate());
		}
		if (event.isTempo() || event.isTimeSignature()) {
			this.span(transport, frame, clock.getSampleRate());
		}
	}

	// the transport is told the song plays over the block, from its first frame, and from the frame of each tempo and time
	// signature reached in it
	private void tell(MidiTransport transport, float rate) {
		if (this.signature == null) {
			MidiTransport.Span last = transport.get(transport.count() - 1);
			this.signature = MidiEvent.timeSignature(this.anchorTick, MidiEvent.ALL_TRACKS, last.getNumerator(), last.getDenominator());
		}
		transport.clear();
		this.span(transport, 0, rate);
	}

	// a span from the frame: the song plays at the tempo, in the time signature, from the quarter note reached there (the
	// song's first measure starts a quarter note in: at tick 960)
	private void span(MidiTransport transport, long frame, float rate) {
		double quarters = (this.anchorTick - TGDuration.QUARTER_TIME + (frame - this.anchorFrame) * MICROSECONDS_PER_QUARTER / (this.usq * (double) rate)) / TGDuration.QUARTER_TIME;
		double tempo = 60000000.0 / this.usq;
		transport.add((int) frame, true, tempo, this.signature.getNumerator(), this.signature.getDenominator(), quarters, Math.round(quarters * 60 / tempo * rate));
	}

	// the channel setup before the position, on the first frame, then the events from the position, anchored at the
	// tempo in force there; the anchors before are forgotten (a loop's next pass keeps them: see wrap)
	private void move(MidiSampleClock clock, long position) throws MidiPlayerException {
		this.moved = false;
		this.events.sort(Comparator.comparingLong(MidiEvent::getTick));
		this.signature = null;
		this.seek(clock, position, 0);
		this.sounding.restart(this.played, position, MICROSECONDS_PER_QUARTER, this.usq * (double) clock.getSampleRate());
	}

	// the events from the position on, played from the frame of the next block at the tempo in force; the channel setup
	// before the position sent on that frame, the last time signature before it kept
	private void seek(MidiSampleClock clock, long position, long frame) throws MidiPlayerException {
		this.next = 0;
		for (; this.next < this.events.size() && this.events.get(this.next).getTick() < position; this.next++) {
			MidiEvent event = this.events.get(this.next);
			if (event.isTimeSignature()) {
				this.signature = event;
			} else if (MidiEventPlayer.isChannelSetup(event.getType())) {
				this.sequencer.sendEvent(event, clock.at((int) frame));
			}
		}
		this.anchorTick = position;
		this.anchorFrame = frame;
	}

	// the tick on the frame of the next block, at the tempo played from there
	private void anchor(long tick, long frame, float rate) {
		this.anchorTick = tick;
		this.anchorFrame = frame;
		this.sounding.anchor(this.played + frame, tick, MICROSECONDS_PER_QUARTER, this.usq * (double) rate);
	}

	private long frameOf(long tick, float rate) {
		return this.anchorFrame + Math.round((tick - this.anchorTick) * this.usq * (double) rate / MICROSECONDS_PER_QUARTER);
	}
}
