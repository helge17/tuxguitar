package org.herac.tuxguitar.player.impl.sequencer;

import org.herac.tuxguitar.player.base.MidiSequenceHandler;
import org.herac.tuxguitar.song.models.TGTimeSignature;

public class MidiSequenceHandlerImpl extends MidiSequenceHandler{
	
	private MidiSequencerImpl seq;
	
	public MidiSequenceHandlerImpl(MidiSequencerImpl seq,int tracks) {
		super(tracks);
		this.seq = seq;
		this.seq.getMidiTrackController().init(getTracks());
	}
	
	public void addControlChange(long tick,int track,int channel, int controller, int value) {
		this.seq.addEvent(MidiEvent.controlChange(tick, track, channel, controller, value));
	}
	
	public void addNoteOff(long tick,int track,int channel, int note, int velocity) {
		this.seq.addEvent(MidiEvent.noteOff(tick, track, channel, note, velocity));
	}
	
	public void addNoteOn(long tick,int track,int channel, int note, int velocity) {
		this.seq.addEvent(MidiEvent.noteOn(tick, track, channel, note, velocity));
	}
	
	public void addPitchBend(long tick,int track,int channel, int value) {
		this.seq.addEvent(MidiEvent.pitchBend(tick, track, channel, value));
	}
	
	public void addProgramChange(long tick,int track,int channel, int instrument) {
		this.seq.addEvent(MidiEvent.programChange(tick, track, channel, instrument));
	}
	
	public void addTempoInUSQ(long tick,int track,int usq) {
		this.seq.addEvent(MidiEvent.tempoInUSQ(tick, usq));
	}
	
	public void addTimeSignature(long tick,int track,TGTimeSignature ts) {
		//not implemented
	}
	
	public void notifyFinish(){
		//not implemented
	}
}
