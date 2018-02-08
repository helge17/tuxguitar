package org.herac.tuxguitar.io.synth;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.herac.tuxguitar.midi.synth.TGAudioBuffer;
import org.herac.tuxguitar.midi.synth.TGSynthModel;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.song.models.TGDuration;

public class TGSynthSequencer {
	
	private static final int SECOND_IN_NANOS = 1000000000;
	private static final long SAMPLE_IN_NANOS = Math.round((TGAudioBuffer.BUFFER_SIZE / 2) * 1000000000.00 / TGAudioBuffer.SAMPLE_RATE);
	
	private long tick;
	private long length;
	private long tempoInUsq;
	private TGSynthModel synth;
	private List<TGSynthEvent> midiEvents;
	private int currentIndex;
	
	public TGSynthSequencer(TGSynthModel synth, List<TGSynthEvent> midiEvents) {
		this.synth = synth;
		this.midiEvents = midiEvents;
	}

	public long getTempoInUsq() {
		return tempoInUsq;
	}

	public void setTempoInUsq(long tempoInUsq) {
		this.tempoInUsq = tempoInUsq;
	}

	public TGSynthModel getSynth() {
		return synth;
	}

	public long getTick() {
		return tick;
	}
	
	public long getLength() {
		return length;
	}

	public void forward() {
		int tempo = (int)((60.00 * 1000.00) / (this.getTempoInUsq() / 1000.00));
		
		this.tick += (TGDuration.QUARTER_TIME * ((float) tempo * (float) SAMPLE_IN_NANOS / 60f) / SECOND_IN_NANOS);
	}
	
	public void start() {
		this.sortEvents();
		this.tempoInUsq = 60000000 / 120;
		this.currentIndex = 0;
		this.tick = 0;
		this.length = (!this.midiEvents.isEmpty() ? this.midiEvents.get(this.midiEvents.size() - 1).getTick() : 0);
	}
	
	public void dispatchEvents() throws MidiPlayerException {
		while( this.currentIndex < this.midiEvents.size() && this.midiEvents.get(this.currentIndex).getTick() <= this.tick ) {
			this.midiEvents.get(this.currentIndex ++).process(this);
			this.waitUntilReady();
		}
	}
	
	private void sortEvents(){
		Collections.sort(this.midiEvents, new Comparator<TGSynthEvent>() {
			public int compare(TGSynthEvent e1, TGSynthEvent e2) {
				if( e1 != null && e2 != null ){
					if(e1.getTick() > e2.getTick()){
						return 1;
					}
					else if(e1.getTick() < e2.getTick()){
						return -1;
					}
				}
				return 0;
			}
		});
	}
	
	public void waitUntilReady() {
		while( this.synth.isBusy() ) {
			Thread.yield();
		}
	}
}
