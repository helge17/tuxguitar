package org.herac.tuxguitar.io.gervill;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiEvent;

import org.herac.tuxguitar.gm.GMChannelRoute;
import org.herac.tuxguitar.gm.GMChannelRouter;
import org.herac.tuxguitar.player.base.MidiSequenceHandler;
import org.herac.tuxguitar.song.models.TGTimeSignature;

public class MidiSequenceHandlerImpl extends MidiSequenceHandler{
	
	private List<MidiEvent> events;
	private GMChannelRouter router;
	
	public MidiSequenceHandlerImpl(int tracks, GMChannelRouter router){
		super(tracks);
		this.router = router;
		this.events = new ArrayList<MidiEvent>();
	}
	
	public void addNoteOff(long tick,int track,int channelId, int note, int velocity, int voice, boolean bendMode) {
		GMChannelRoute gmChannel = this.router.getRoute(channelId);
		if( gmChannel != null ){
			this.events.add(new MidiEvent(MidiMessageUtils.noteOff(resolveChannel(gmChannel,bendMode), note, velocity), tick ));
		}
	}
	
	public void addNoteOn(long tick,int track,int channelId, int note, int velocity, int voice, boolean bendMode) {
		GMChannelRoute gmChannel = this.router.getRoute(channelId);
		if( gmChannel != null ){
			this.events.add(new MidiEvent(MidiMessageUtils.noteOn(resolveChannel(gmChannel,bendMode), note, velocity), tick ));
		}
	}
	
	public void addPitchBend(long tick,int track,int channelId, int value, int voice, boolean bendMode) {
		GMChannelRoute gmChannel = this.router.getRoute(channelId);
		if( gmChannel != null ){
			this.events.add(new MidiEvent(MidiMessageUtils.pitchBend(resolveChannel(gmChannel,bendMode), value), tick ));
		}
	}
	
	public void addControlChange(long tick,int track,int channelId, int controller, int value) {
		GMChannelRoute gmChannel = this.router.getRoute(channelId);
		if( gmChannel != null ){
			this.events.add(new MidiEvent(MidiMessageUtils.controlChange(gmChannel.getChannel1(), controller, value), tick ));
			if( gmChannel.getChannel1() != gmChannel.getChannel2() ){
				this.events.add(new MidiEvent(MidiMessageUtils.controlChange(gmChannel.getChannel2(), controller, value), tick ));
			}
		}
	}
	
	public void addProgramChange(long tick,int track,int channelId, int instrument) {
		GMChannelRoute gmChannel = this.router.getRoute(channelId);
		if( gmChannel != null ){
			this.events.add(new MidiEvent(MidiMessageUtils.programChange(gmChannel.getChannel1(), instrument), tick ));
			if( gmChannel.getChannel1() != gmChannel.getChannel2() ){
				this.events.add(new MidiEvent(MidiMessageUtils.programChange(gmChannel.getChannel2(), instrument), tick ));
			}
		}
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
	
	public List<MidiEvent> getEvents(){
		return this.events;
	}
	
	private int resolveChannel(GMChannelRoute gmChannel, boolean bendMode){
		return (bendMode ? gmChannel.getChannel2() : gmChannel.getChannel1());
	}
}
