package app.tuxguitar.io.midi;

import java.io.IOException;
import java.io.OutputStream;

import app.tuxguitar.gm.GMChannelRoute;
import app.tuxguitar.gm.GMChannelRouter;
import app.tuxguitar.io.midi.base.MidiEvent;
import app.tuxguitar.io.midi.base.MidiSequence;
import app.tuxguitar.io.midi.base.MidiTrack;
import app.tuxguitar.player.base.MidiSequenceHandler;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGTimeSignature;

public class MidiSequenceHandlerImpl extends MidiSequenceHandler{

	private OutputStream stream;
	private MidiSequence sequence;
	private GMChannelRouter router;

	public MidiSequenceHandlerImpl(int tracks, GMChannelRouter router, OutputStream stream){
		super(tracks);
		this.router = router;
		this.stream = stream;
		this.init();
	}

	private void init(){
		this.sequence = new MidiSequence(MidiSequence.PPQ,(int) TGDuration.QUARTER_TIME);
		for (int i = 0; i < getTracks(); i++) {
			this.sequence.addTrack(new MidiTrack());
		}
	}

	public MidiSequence getSequence(){
		return this.sequence;
	}

	private int resolveChannel(GMChannelRoute gmChannel, boolean bendMode){
		return (bendMode ? gmChannel.getChannel2() : gmChannel.getChannel1());
	}

	public void addEvent(int track, MidiEvent event) {
		if(track >= 0 && track < getSequence().countTracks()){
			getSequence().getTrack(track).add(event);
		}
	}

	public void addNoteOff(long tick,int track,int channelId, int note, int velocity, int voice, boolean bendMode) {
		GMChannelRoute gmChannel = this.router.getRoute(channelId);
		if( gmChannel != null ){
			addEvent(track,new MidiEvent(MidiMessageUtils.noteOff(resolveChannel(gmChannel,bendMode), note, velocity), tick ));
		}
	}

	public void addNoteOn(long tick,int track,int channelId, int note, int velocity, int voice, boolean bendMode) {
		GMChannelRoute gmChannel = this.router.getRoute(channelId);
		if( gmChannel != null ){
			addEvent(track,new MidiEvent(MidiMessageUtils.noteOn(resolveChannel(gmChannel,bendMode), note, velocity), tick ));
		}
	}

	public void addPitchBend(long tick,int track,int channelId, int value, int voice, boolean bendMode) {
		GMChannelRoute gmChannel = this.router.getRoute(channelId);
		if( gmChannel != null ){
			addEvent(track,new MidiEvent(MidiMessageUtils.pitchBend(resolveChannel(gmChannel,bendMode), value), tick ));
		}
	}

	public void addControlChange(long tick,int track,int channelId, int controller, int value) {
		GMChannelRoute gmChannel = this.router.getRoute(channelId);
		if( gmChannel != null ){
			addEvent(track,new MidiEvent(MidiMessageUtils.controlChange(gmChannel.getChannel1(), controller, value), tick ));
			if( gmChannel.getChannel1() != gmChannel.getChannel2() ){
				addEvent(track,new MidiEvent(MidiMessageUtils.controlChange(gmChannel.getChannel2(), controller, value), tick ));
			}
		}
	}

	public void addProgramChange(long tick,int track,int channelId, int instrument) {
		GMChannelRoute gmChannel = this.router.getRoute(channelId);
		if( gmChannel != null ){
			addEvent(track,new MidiEvent(MidiMessageUtils.programChange(gmChannel.getChannel1(), instrument), tick ));
			if( gmChannel.getChannel1() != gmChannel.getChannel2() ){
				addEvent(track,new MidiEvent(MidiMessageUtils.programChange(gmChannel.getChannel2(), instrument), tick ));
			}
		}
	}

	public void addTrackName(long tick, int track, String name) {
		addEvent(track, new MidiEvent(MidiMessageUtils.trackName(name), tick));
	}

	public void addTempoInUSQ(long tick,int track,int usq) {
		addEvent(track,new MidiEvent(MidiMessageUtils.tempoInUSQ(usq), tick ));
	}

	public void addTimeSignature(long tick,int track,TGTimeSignature ts) {
		addEvent(track,new MidiEvent(MidiMessageUtils.timeSignature(ts), tick ));
	}

	public void notifyFinish() {
		try {
			getSequence().finish();
			new MidiFileWriter().write(getSequence(),1,this.stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
