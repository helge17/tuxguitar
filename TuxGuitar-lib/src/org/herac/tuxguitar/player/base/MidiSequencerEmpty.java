package org.herac.tuxguitar.player.base;

import org.herac.tuxguitar.song.models.TGTimeSignature;

public class MidiSequencerEmpty implements MidiSequencer{
	
	public void open() {
		// Not implemented
	}	
	
	public void close() {
		// Not implemented
	}
	
	public void check() {
		// Not implemented
	}
	
	public MidiSequenceHandler createSequence(int tracks) {
		return new MidiSequenceHandler(tracks) {
			
			public void notifyFinish() {
				// Not implemented
			}
			
			public void addTimeSignature(long tick, int track, TGTimeSignature ts) {
				// Not implemented
			}
			
			public void addTempoInUSQ(long tick, int track, int usq) {
				// Not implemented
			}
			
			public void addProgramChange(long tick, int track, int channel,int instrument) {
				// Not implemented
			}
			
			public void addPitchBend(long tick, int track, int channel, int value, int voice, boolean bendMode) {
				// Not implemented
			}
			
			public void addNoteOn(long tick, int track, int channel, int note,int velocity, int voice, boolean bendMode) {
				// Not implemented
			}
			
			public void addNoteOff(long tick, int track, int channel, int note,int velocity, int voice, boolean bendMode) {
				// Not implemented
			}
			
			public void addControlChange(long tick, int track, int channel,int controller, int value) {
				// Not implemented
			}
		};
	}
	
	public void setTransmitter(MidiTransmitter transmitter) throws MidiPlayerException {
		// Not implemented
	}
	
	public long getTickLength() {
		// Not implemented
		return 0;
	}
	
	public long getTickPosition() {
		// Not implemented
		return 0;
	}
	
	public boolean isRunning() {
		// Not implemented
		return false;
	}
	
	public void setMute(int index, boolean mute) {
		//not implemented
	}
	
	public void setSolo(int index, boolean solo) {
		// Not implemented
	}
	
	public void setTickPosition(long tickPosition) {
		// Not implemented
	}
	
	public void start() {
		// Not implemented
	}
	
	public void stop() {
		// Not implemented
	}
	
	public String getKey() {
		return null;
	}
	
	public String getName() {
		return null;
	}	
}
