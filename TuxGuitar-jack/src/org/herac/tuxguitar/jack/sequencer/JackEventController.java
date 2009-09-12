package org.herac.tuxguitar.jack.sequencer;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.song.models.TGDuration;

public class JackEventController{
	private JackSequencer sequencer;
	private List events;
	
	private double tick;
	private double lastTick;
	private boolean reset;
	
	public JackEventController(JackSequencer sequencer){
		this.sequencer = sequencer;
		this.events = new ArrayList();
		this.reset();
	}
	
	public void process() throws MidiPlayerException {
		this.lastTick = this.tick;
		this.tick = this.sequencer.getJackTickController().getTick();
		for(int i = 0;i < this.events.size();i ++){
			JackEvent event = (JackEvent)this.events.get(i);
			if(shouldSend(event,this.tick,this.lastTick)){
				this.sequencer.sendEvent(event);
			}
		}
		this.reset = false;
	}
	
	private boolean shouldSend(JackEvent event,double tick,double lastTick){
		if(event.getTick() > tick){
			return false;
		}
		if(event.getTrack() != JackEvent.ALL_TRACKS){
			if(this.sequencer.getJackTrackController().isMute(event.getTrack())){
				return false;
			}
			if(this.sequencer.getJackTrackController().isAnySolo() && !this.sequencer.getJackTrackController().isSolo(event.getTrack())){
				return false;
			}
		}
		if(this.reset){
			if(event.getType() == JackEvent.MIDI_SYSTEM_EVENT){
				return true;
			}
			if(event.getType() == JackEvent.MIDI_EVENT_CONTROL_CHANGE){
				return true;
			}
			if(event.getType() == JackEvent.MIDI_EVENT_PROGRAM_CHANGE){
				return true;
			}
		}
		return (event.getTick() > lastTick);
	}
	
	public List getEvents(){
		return this.events;
	}
	
	public void addEvent(JackEvent event){
		this.events.add(event);
	}
	
	public void clearEvents(){
		this.events.clear();
	}
	
	public void reset(){
		this.tick = (this.sequencer.getTickPosition() - (TGDuration.QUARTER_TIME / 8) );
		this.reset = true;
	}
	
	public List getTempoChanges(){
		List tempoChanges = new ArrayList();
		for(int i = 0; i < this.events.size(); i ++){
			JackEvent event = (JackEvent) events.get(i);				
			if(event.getType() == JackEvent.MIDI_SYSTEM_EVENT){
				if(event.getData()[0] == 0x51){
					int usq = ((event.getData()[1] & 0xff) | ((event.getData()[2] & 0xff) << 8) | ((event.getData()[3] & 0xff) << 16));
					long[] tempoChange = new long[2];
					tempoChange[0] = event.getTick();
					tempoChange[1] = ((long)((60.00 * 1000.00) / (usq / 1000.00)) );
					tempoChanges.add( tempoChange );
				}
			}
		}
		return tempoChanges;
	}
}
