package app.tuxguitar.player.impl.sequencer;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.player.base.MidiPlayerException;

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
			MidiEvent event = this.events.get(i);
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
		if(event.getTick() > lastTick) {
			return this.sequencer.getMidiTrackController().isAudible(event.getTrack());
		}
		return (this.reset && isChannelSetup(event.getType()));
	}

	// the events resent after a reset, for the channels to be set up as they were
	static boolean isChannelSetup(int type){
		return (type == MidiEvent.MIDI_SYSTEM_EVENT || type == MidiEvent.MIDI_EVENT_CONTROL_CHANGE || type == MidiEvent.MIDI_EVENT_PROGRAM_CHANGE);
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
