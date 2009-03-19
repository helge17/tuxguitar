package org.herac.tuxguitar.io.gervill;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiEvent;

import org.herac.tuxguitar.player.base.MidiSequenceHandler;
import org.herac.tuxguitar.song.models.TGTimeSignature;

public class MidiSequenceHandlerImpl extends MidiSequenceHandler{
	
	private List events;
	
	public MidiSequenceHandlerImpl(int tracks ){
		super(tracks);
		this.events = new ArrayList();
	}
	
	public void addControlChange(long tick,int track,int channel, int controller, int value) {
		this.events.add(new MidiEvent(MidiMessageUtils.controlChange(channel, controller, value), tick ));
	}
	
	public void addNoteOff(long tick,int track,int channel, int note, int velocity) {
		this.events.add(new MidiEvent(MidiMessageUtils.noteOff(channel, note, velocity), tick ));
	}
	
	public void addNoteOn(long tick,int track,int channel, int note, int velocity) {
		this.events.add(new MidiEvent(MidiMessageUtils.noteOn(channel, note, velocity), tick ));
	}
	
	public void addPitchBend(long tick,int track,int channel, int value) {
		this.events.add(new MidiEvent(MidiMessageUtils.pitchBend(channel, value), tick ));
	}
	
	public void addProgramChange(long tick,int track,int channel, int instrument) {
		this.events.add(new MidiEvent(MidiMessageUtils.programChange(channel, instrument), tick ));
	}
	
	public void addTempoInUSQ(long tick,int track,int usq) {
		this.events.add(new MidiEvent(MidiMessageUtils.tempoInUSQ(usq), tick ));
	}
	
	public void addTimeSignature(long tick,int track,TGTimeSignature ts) {
		this.events.add(new MidiEvent(MidiMessageUtils.timeSignature(ts), tick ));
	}
	
	public void notifyFinish() {
		// not implemented
	}
	
	public List getEvents(){
		return this.events;
	}
}
