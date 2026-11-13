package app.tuxguitar.player.impl.sequencer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiSampleClock;
import app.tuxguitar.player.base.MidiTransport;
import app.tuxguitar.song.models.TGDuration;

/**
 * Plays the sequence on a sample clock, a block of frames at a time: each event whose tick sounds in the block goes to
 * the clock's receiver of its frame. A tick sounds on the frame its time from the anchor reaches at the tempo, rounded
 * to the nearest frame. Playing from a position anchors it on the first frame of the next block, and sends again the
 * channel setup before it there; a tempo event moves the anchor to its own tick and frame. A time signature is sent to
 * no receiver: the clock's transport is told it from its frame on.
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

	// playing goes on from the sequencer's position, with the next block
	void reset() {
		this.moved = true;
	}

	// sends the events of the next block and moves the sequencer's position to the tick reached at its end
	void process(MidiSampleClock clock, int frames) throws MidiPlayerException {
		float rate = clock.getSampleRate();
		if (this.moved) {
			this.move(clock, this.sequencer.getTickPosition());
		}
		MidiTransport transport = (clock.getTransport() != null ? clock.getTransport() : this.untold);
		this.tell(transport, rate);
		for (; this.next < this.events.size(); this.next++) {
			MidiEvent event = this.events.get(this.next);
			long frame = this.frameOf(event.getTick(), rate);
			if (frame >= frames) {
				break;
			}
			if (event.isTempo()) {
				this.anchor(event.getTick(), frame);
			}
			if (event.isTimeSignature()) {
				this.signature = event;
				this.span(transport, frame, rate);
			} else if (this.sequencer.getMidiTrackController().isAudible(event.getTrack())) {
				this.sequencer.sendEvent(event, clock.at((int) frame));
			}
		}
		this.sequencer.getMidiTickPlayer().setTick(this.tickAt(frames, rate));
		this.anchorFrame -= frames;
	}

	// the transport is told the song plays over the block, from its first frame, and from the frame of each time signature
	// reached in it
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

	// the channel setup before the position, on the first frame, then the events from the position
	private void move(MidiSampleClock clock, long position) throws MidiPlayerException {
		this.moved = false;
		this.events.sort(Comparator.comparingLong(MidiEvent::getTick));
		this.anchor(position, 0);
		this.signature = null;
		this.next = 0;
		for (; this.next < this.events.size() && this.events.get(this.next).getTick() < position; this.next++) {
			MidiEvent event = this.events.get(this.next);
			if (event.isTimeSignature()) {
				this.signature = event;
			} else if (MidiEventPlayer.isChannelSetup(event.getType())) {
				this.sequencer.sendEvent(event, clock.at(0));
			}
		}
	}

	private void anchor(long tick, long frame) {
		this.anchorTick = tick;
		this.anchorFrame = frame;
	}

	private long frameOf(long tick, float rate) {
		return this.anchorFrame + Math.round((tick - this.anchorTick) * this.usq * (double) rate / MICROSECONDS_PER_QUARTER);
	}

	private long tickAt(long frame, float rate) {
		return this.anchorTick + (long) Math.floor((frame - this.anchorFrame) * MICROSECONDS_PER_QUARTER / (this.usq * (double) rate));
	}
}
