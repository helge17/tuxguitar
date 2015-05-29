package org.herac.tuxguitar.player.impl.sequencer;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.player.base.MidiPlayerException;

public class MidiEventPlayer{
	
	private MidiSequencerImpl sequencer;
	private List<MidiEvent> events;
	
	private long tick;
	private long lastTick;
	private boolean reset;
	
	public MidiEventPlayer(MidiSequencerImpl sequencer){
		this.sequencer = sequencer;
		this.events = new ArrayList<MidiEvent>();
		this.reset();
	}
	
	public void process() throws MidiPlayerException {
		this.lastTick = this.tick;
		this.tick = this.sequencer.getTickPosition();
		for(int i = 0;i < this.events.size();i ++){
			MidiEvent event = (MidiEvent)this.events.get(i);
			if(shouldSend(event,this.tick,this.lastTick)){
				this.sequencer.sendEvent(event);
			}
		}
		this.reset = false;
	}
	
	private boolean shouldSend(MidiEvent event,long tick,long lastTick){
		if(event.getTick() > tick){
			return false;
		}
		if(event.getTrack() != MidiEvent.ALL_TRACKS){
			if(this.sequencer.getMidiTrackController().isMute(event.getTrack())){
				return false;
			}
			if(this.sequencer.getMidiTrackController().isAnySolo() && !this.sequencer.getMidiTrackController().isSolo(event.getTrack())){
				return false;
			}
		}
		if(this.reset){
			if(event.getType() == MidiEvent.MIDI_SYSTEM_EVENT){
				return true;
			}
			if(event.getType() == MidiEvent.MIDI_EVENT_CONTROL_CHANGE){
				return true;
			}
			if(event.getType() == MidiEvent.MIDI_EVENT_PROGRAM_CHANGE){
				return true;
			}
		}
		return (event.getTick() > lastTick);
	}
	
	public void addEvent(MidiEvent event){
		this.events.add(event);
	}
	
	public void clearEvents(){
		this.events.clear();
	}
	
	public void reset(){
		this.tick = (this.sequencer.getTickPosition() - 1);
		this.reset = true;
	}
}
