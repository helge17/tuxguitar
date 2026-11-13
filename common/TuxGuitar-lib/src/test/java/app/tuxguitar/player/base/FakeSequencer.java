package app.tuxguitar.player.base;

import java.util.List;

/**
 * A sequencer that logs what it is told to do: "start", "stop" and "seek position"; it keeps the sample clock it is
 * given. It runs only when a test says so; a broken one fails to check, start, seek and read its position. Its sequence
 * is SEQUENCE_LENGTH ticks long.
 */
class FakeSequencer implements MidiSequencer {

	static final long SEQUENCE_LENGTH = 16320;
	// how a failure of the sequencer is printed, before its message ("cannot seek", "cannot start", ...)
	static final String FAILURE = MidiPlayerException.class.getName() + ": ";

	private final List<String> log;
	long position = 1234;
	boolean running = false;
	boolean broken = false;
	// the last sequence it made
	MidiEventRecorder sequence;
	// the sample clock it was given, and the one it last started on (null: its timer)
	MidiSampleClock clock;
	MidiSampleClock startedOn;

	FakeSequencer(List<String> log) {
		this.log = log;
	}

	public String getKey() { return "fake"; }
	public String getName() { return "fake"; }
	public void open() { }
	public void close() { }
	public void setTransmitter(MidiTransmitter transmitter) { }
	public MidiSequenceHandler createSequence(int tracks) { this.sequence = new MidiEventRecorder(tracks); return this.sequence; }
	public void setSolo(int index, boolean solo) { }
	public void setMute(int index, boolean mute) { }
	public boolean isRunning() { return this.running; }
	public long getTickLength() { return SEQUENCE_LENGTH; }

	public void check() throws MidiPlayerException {
		fail("check");
	}

	public long getTickPosition() throws MidiPlayerException {
		fail("read");
		return this.position;
	}

	public void start() throws MidiPlayerException {
		fail("start");
		log.add("start");
		this.startedOn = this.clock;
	}

	public void setClock(MidiSampleClock clock) {
		this.clock = clock;
	}

	public void stop() {
		log.add("stop");
		this.running = false;
	}

	public void setTickPosition(long tickPosition) throws MidiPlayerException {
		fail("seek");
		log.add("seek " + tickPosition);
		this.position = tickPosition;
	}

	private void fail(String action) throws MidiPlayerException {
		if (this.broken) {
			throw new MidiPlayerException("cannot " + action);
		}
	}
}
