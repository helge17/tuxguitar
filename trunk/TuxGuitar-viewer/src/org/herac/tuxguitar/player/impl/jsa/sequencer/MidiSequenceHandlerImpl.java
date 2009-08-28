package org.herac.tuxguitar.player.impl.jsa.sequencer;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import org.herac.tuxguitar.player.base.MidiSequenceHandler;
import org.herac.tuxguitar.player.impl.jsa.utils.MidiMessageUtils;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGTimeSignature;

public class MidiSequenceHandlerImpl extends MidiSequenceHandler{
	
	private MidiSequenceLoader loader;
	private Sequence sequence;
	private Track[] midiTracks;
	
	public MidiSequenceHandlerImpl(MidiSequenceLoader loader,int tracks){
		super(tracks);
		this.loader = loader;
		this.init();
	}
	
	private void init(){
		try {
			this.sequence = new Sequence(Sequence.PPQ,(int)TGDuration.QUARTER_TIME);
			this.midiTracks = new Track[getTracks()];
			for (int i = 0; i < this.midiTracks.length; i++) {
				this.midiTracks[i] = this.sequence.createTrack();
			}
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}
	
	public Sequence getSequence(){
		return this.sequence;
	}
	
	public void addEvent(int track, MidiEvent event) {
		if(track >= 0 && track < this.midiTracks.length){
			this.midiTracks[track].add(event);
		}
	}
	
	public void addControlChange(long tick,int track,int channel, int controller, int value) {
		addEvent(track,new MidiEvent(MidiMessageUtils.controlChange(channel, controller, value), tick ));
	}
	
	public void addNoteOff(long tick,int track,int channel, int note, int velocity) {
		addEvent(track,new MidiEvent(MidiMessageUtils.noteOff(channel, note, velocity), tick ));
	}
	
	public void addNoteOn(long tick,int track,int channel, int note, int velocity) {
		addEvent(track,new MidiEvent(MidiMessageUtils.noteOn(channel, note, velocity), tick ));
	}
	
	public void addPitchBend(long tick,int track,int channel, int value) {
		addEvent(track,new MidiEvent(MidiMessageUtils.pitchBend(channel, value), tick ));
	}
	
	public void addProgramChange(long tick,int track,int channel, int instrument) {
		addEvent(track,new MidiEvent(MidiMessageUtils.programChange(channel, instrument), tick ));
	}
	
	public void addTempoInUSQ(long tick,int track,int usq) {
		addEvent(track,new MidiEvent(MidiMessageUtils.tempoInUSQ(usq), tick ));
	}
	
	public void addTimeSignature(long tick,int track,TGTimeSignature ts) {
		addEvent(track,new MidiEvent(MidiMessageUtils.timeSignature(ts), tick ));
	}
	
	public void notifyFinish(){
		this.loader.setSequence(getSequence());
	}
}
