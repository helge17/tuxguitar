package org.herac.tuxguitar.player.base;


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
	
}
