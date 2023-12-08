package org.herac.tuxguitar.io.synth;

import org.herac.tuxguitar.midi.synth.TGSynthChannel;
import org.herac.tuxguitar.player.base.MidiPlayerException;

public abstract class TGSynthEvent {
	
	private long tick;
	
	public TGSynthEvent(long tick) {
		this.tick = tick;
	}
	
	public long getTick() {
		return tick;
	}
	
	public abstract void process(TGSynthSequencer context) throws MidiPlayerException;
	
	public static TGSynthEvent noteOn(final long tick, final int channelId, final int key, final int velocity, final int voice, final boolean bendMode) {
		return new TGSynthEvent(tick) {
			public void process(TGSynthSequencer context) throws MidiPlayerException {
				TGSynthChannel channel = context.getSynth().getChannelById(channelId);
				if( channel != null ) {
					channel.sendNoteOn(key, velocity, voice, bendMode);
				}
			}
		};
	}
	
	public static TGSynthEvent noteOff(final long tick, final int channelId, final int key, final int velocity, final int voice, final boolean bendMode) {
		return new TGSynthEvent(tick) {
			public void process(TGSynthSequencer context) throws MidiPlayerException {
				TGSynthChannel channel = context.getSynth().getChannelById(channelId);
				if( channel != null ) {
					channel.sendNoteOff(key, velocity, voice, bendMode);
				}
			}
		};
	}
	
	public static TGSynthEvent pitchBend(long tick, final int channelId, final int value, final int voice, final boolean bendMode){
		return new TGSynthEvent(tick) {
			public void process(TGSynthSequencer context) throws MidiPlayerException {
				TGSynthChannel channel = context.getSynth().getChannelById(channelId);
				if( channel != null ) {
					channel.sendPitchBend(value, voice, bendMode);
				}
			}
		};
	}
	
	public static TGSynthEvent controlChange(long tick, final int channelId, final int controller, final int value){
		return new TGSynthEvent(tick) {
			public void process(TGSynthSequencer context) throws MidiPlayerException {
				TGSynthChannel channel = context.getSynth().getChannelById(channelId);
				if( channel != null ) {
					channel.sendControlChange(controller, value);
				}
			}
		};
	}
	
	public static TGSynthEvent programChange(long tick, final int channelId, final int value){
		return new TGSynthEvent(tick) {
			public void process(TGSynthSequencer context) throws MidiPlayerException {
				TGSynthChannel channel = context.getSynth().getChannelById(channelId);
				if( channel != null ) {
					channel.sendProgramChange(value);
				}
			}
		};
	}
	
	public static TGSynthEvent tempoInUSQ(long tick, final int usq) {
		return new TGSynthEvent(tick) {
			public void process(TGSynthSequencer context) throws MidiPlayerException {
				context.setTempoInUsq(usq);
			}
		};
	}
}
