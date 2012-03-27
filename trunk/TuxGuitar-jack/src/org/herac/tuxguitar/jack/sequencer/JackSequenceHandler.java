package org.herac.tuxguitar.jack.sequencer;

import org.herac.tuxguitar.player.base.MidiSequenceHandler;
import org.herac.tuxguitar.song.models.TGTimeSignature;

public class JackSequenceHandler extends MidiSequenceHandler{
	
	private JackSequencer seq;
	
	public JackSequenceHandler(JackSequencer seq,int tracks) {
		super(tracks);
		this.seq = seq;
		this.seq.getJackTrackController().init(getTracks());
	}
	
	public void addNoteOff(long tick,int track,int channel, int note, int velocity,int voice,boolean bendMode) {
		this.seq.addEvent(JackEvent.noteOff(tick, track, channel, note, velocity, voice, bendMode));
	}
	
	public void addNoteOn(long tick,int track,int channel, int note, int velocity,int voice,boolean bendMode) {
		this.seq.addEvent(JackEvent.noteOn(tick, track, channel, note, velocity, voice, bendMode));
	}
	
	public void addPitchBend(long tick,int track,int channel, int value,int voice,boolean bendMode) {
		this.seq.addEvent(JackEvent.pitchBend(tick, track, channel, value, voice, bendMode));
	}
	
	public void addControlChange(long tick,int track,int channel, int controller, int value) {
		this.seq.addEvent(JackEvent.controlChange(tick, track, channel, controller, value));
	}
	
	public void addProgramChange(long tick,int track,int channel, int instrument) {
		this.seq.addEvent(JackEvent.programChange(tick, track, channel, instrument));
	}
	
	public void addTempoInUSQ(long tick,int track,int usq) {
		this.seq.addEvent(JackEvent.tempoInUSQ(tick, usq));
	}
	
	public void addTimeSignature(long tick,int track,TGTimeSignature ts) {
		//not implemented
	}
	
	public void notifyFinish(){
		//not implemented
	}
}
