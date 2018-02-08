package org.herac.tuxguitar.midi.synth;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.player.base.MidiPlayerException;

public class TGSynthChannelPendingQueue {
	
	private TGSynthChannel channel;
	private List<MidiEvent> queue;
	
	public TGSynthChannelPendingQueue(TGSynthChannel channel) {
		this.channel = channel;
		this.queue = new ArrayList<MidiEvent>();
	}
	
	public void clear() {
		this.queue.clear();
	}
	
	public void dispatch() throws MidiPlayerException {
		List<MidiEvent> events = new ArrayList<MidiEvent>(this.queue);
		this.clear();
		while(!events.isEmpty()) {
			MidiEvent midiEvent = events.remove(0);
			midiEvent.process(this.channel);
		}
	}
	
	public void addControlChange(final int controller, final int value) {
		this.queue.add(new MidiEvent() {
			public void process(TGSynthChannel channel) throws MidiPlayerException {
				channel.sendControlChange(controller, value);
			}
		});
	}
	
	public void addPitchBend(final int value, final int voice, final boolean bendMode) throws MidiPlayerException {
		this.queue.add(new MidiEvent() {
			public void process(TGSynthChannel channel) throws MidiPlayerException {
				channel.sendPitchBend(value, voice, bendMode);
			}
		});
	}
	
	private interface MidiEvent {
		void process(TGSynthChannel channel) throws MidiPlayerException;
	}
}
