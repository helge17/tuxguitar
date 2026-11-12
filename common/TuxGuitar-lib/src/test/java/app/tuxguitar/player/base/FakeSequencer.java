package app.tuxguitar.player.base;

import java.util.List;

/**
 * A sequencer that logs what it is told to do: "start", "stop" and "seek position". It runs only when a test says
 * so; a broken one fails to check, start, seek and read its position. Its sequence is SEQUENCE_LENGTH ticks long.
 */
class FakeSequencer implements MidiSequencer {

	static final long SEQUENCE_LENGTH = 16320;

	private final List<String> log;
	long position = 1234;
	boolean running = false;
	boolean broken = false;

	FakeSequencer(List<String> log) {
		this.log = log;
	}

	public String getKey() { return "fake"; }
	public String getName() { return "fake"; }
	public void open() { }
	public void close() { }
	public void setTransmitter(MidiTransmitter transmitter) { }
	public MidiSequenceHandler createSequence(int tracks) { return new MidiEventRecorder(tracks); }
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
