package org.herac.tuxguitar.io.synth;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.player.base.MidiSequenceHandler;
import org.herac.tuxguitar.song.models.TGTimeSignature;

public class TGSynthSequenceHandler extends MidiSequenceHandler{
	
	private List<TGSynthEvent> events;
	
	public TGSynthSequenceHandler(int tracks) {
		super(tracks);
		
		this.events = new ArrayList<TGSynthEvent>();
	}
	
	public void addNoteOn(long tick,int track,int channelId, int key, int velocity, int voice, boolean bendMode) {
		this.events.add(TGSynthEvent.noteOn(tick, channelId, key, velocity, voice, bendMode));
	}
	
	public void addNoteOff(long tick,int track,int channelId, int key, int velocity, int voice, boolean bendMode) {
		this.events.add(TGSynthEvent.noteOff(tick, channelId, key, velocity, voice, bendMode));
	}
	
	public void addPitchBend(long tick,int track,int channelId, int value, int voice, boolean bendMode) {
		this.events.add(TGSynthEvent.pitchBend(tick, channelId, value, voice, bendMode));
	}
	
	public void addControlChange(long tick,int track,int channelId, int controller, int value) {
		this.events.add(TGSynthEvent.controlChange(tick, channelId, controller, value));
	}
	
	public void addProgramChange(long tick,int track,int channelId, int value) {
		this.events.add(TGSynthEvent.programChange(tick, channelId, value));
	}
	
	public void addTempoInUSQ(long tick,int track,int usq) {
		this.events.add(TGSynthEvent.tempoInUSQ(tick, usq));
	}
	
	public void addTimeSignature(long tick,int track,TGTimeSignature ts) {
		// not implemented
	}
	
	public void notifyFinish() {
		// not implemented
	}
	
	public List<TGSynthEvent> getEvents(){
		return this.events;
	}
}
