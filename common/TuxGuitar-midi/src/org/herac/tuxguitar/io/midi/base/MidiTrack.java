package org.herac.tuxguitar.io.midi.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MidiTrack {
	
	private long ticks;
	private List<MidiEvent> events = new ArrayList<MidiEvent>();
	
	public void add(MidiEvent event) {
		this.events.add(event);
		this.ticks = Math.max(this.ticks,event.getTick());
	}
	
	public MidiEvent get(int index){
		return (MidiEvent)this.events.get(index);
	}
	
	public int size(){
		return this.events.size();
	}
	
	public long ticks(){
		return this.ticks;
	}
	
	public void sort(){
		final Comparator<MidiEvent> comparator = new Comparator<MidiEvent>() {
			public int compare(MidiEvent e1, MidiEvent e2) {
				if( e1 != null && e2 != null ){
					if(e1.getTick() == e2.getTick()){
						return 0;
					}
					return (e1.getTick() > e2.getTick() ? 1 : -1);
				}
				return 0;
			}
		};
		Collections.sort(this.events,comparator);
	}
}
