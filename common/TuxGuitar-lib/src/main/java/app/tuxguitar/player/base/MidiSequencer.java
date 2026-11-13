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
}
