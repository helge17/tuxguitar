package app.tuxguitar.player.base;


public interface MidiSequencer extends MidiDevice{

	public void start() throws MidiPlayerException;

	public void stop() throws MidiPlayerException;

	public boolean isRunning() throws MidiPlayerException;

	public void setTickPosition(long tickPosition) throws MidiPlayerException;

	public long getTickPosition() throws MidiPlayerException;

	public long getTickLength() throws MidiPlayerException;

	public void setTransmitter( MidiTransmitter transmitter) throws MidiPlayerException;

	public MidiSequenceHandler createSequence(int tracks) throws MidiPlayerException;

	public void setSolo(int index,boolean solo) throws MidiPlayerException;

	public void setMute(int index,boolean mute) throws MidiPlayerException;

	// the sample clock to play on from the next start, or null to play on the sequencer's own timer; a sequencer that
	// cannot play on a sample clock keeps its timer
	public default void setClock(MidiSampleClock clock) throws MidiPlayerException {
	}

	// where the sequencer has sent its events up to; on a sample clock the output sounds that later than the position
	public default long getScheduledTickPosition() throws MidiPlayerException {
		return this.getTickPosition();
	}

	// from now on, each time the sequence's end is reached it plays again from the tick (a negative one: it ends there);
	// true when the sequencer does that itself, on the very frame the end sounds on (on a sample clock), false when the
	// player has to start the next pass
	public default boolean loop(long start) throws MidiPlayerException {
		return false;
	}

	// how many times the sequence started again by itself (see loop), since the sequencer was made
	public default int getPasses() throws MidiPlayerException {
		return 0;
	}

	// the events of the next pass, which plays them in place of this pass's from where it starts again (a Training Mode
	// pass at its own tempo); the passes after it play them too, unless another is given. Only for a sequencer that
	// loops by itself
	public default MidiSequenceHandler createNextPass(int tracks) throws MidiPlayerException {
		throw new MidiPlayerException("this sequencer does not play the next pass of a loop by itself");
	}
}
