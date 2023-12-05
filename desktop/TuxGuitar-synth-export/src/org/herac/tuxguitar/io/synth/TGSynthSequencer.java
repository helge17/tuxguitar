package org.herac.tuxguitar.io.synth;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.herac.tuxguitar.midi.synth.TGAudioBuffer;
import org.herac.tuxguitar.midi.synth.TGSynthModel;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.song.models.TGDuration;

public class TGSynthSequencer {
	
	private static final double SECOND_IN_NANOS = 1000000000.00;
	private static final double SAMPLE_IN_NANOS = ((TGAudioBuffer.BUFFER_SIZE / 2) * SECOND_IN_NANOS / TGAudioBuffer.SAMPLE_RATE);
	
	private long length;
	private long tempoInUsq;
	private TGSynthModel synth;
	private List<TGSynthEvent> midiEvents;
	private BigDecimal tick;
	private int currentIndex;
	
	public TGSynthSequencer(TGSynthModel synth, List<TGSynthEvent> midiEvents) {
		this.synth = synth;
		this.midiEvents = midiEvents;
		this.tick = new BigDecimal(0);
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
	
	public long getLength() {
		return length;
	}

	public boolean isEnded() {
		return (this.tick.longValue() >= this.length);
	}
	
	public void forward() {
		double tempo = ((60.00 * 1000.00) / (this.getTempoInUsq() / 1000.00));
		
		this.tick = this.tick.add(new BigDecimal(TGDuration.QUARTER_TIME * (tempo * SAMPLE_IN_NANOS / 60.00) / SECOND_IN_NANOS));
	}
	
	public void start() {
		this.sortEvents();
		this.tempoInUsq = 60000000 / 120;
		this.currentIndex = 0;
		this.tick = new BigDecimal(0);
		this.length = (!this.midiEvents.isEmpty() ? this.midiEvents.get(this.midiEvents.size() - 1).getTick() : 0);
	}
	
	public void dispatchEvents() throws MidiPlayerException {
		while( this.currentIndex < this.midiEvents.size() && this.midiEvents.get(this.currentIndex).getTick() <= this.tick.longValue() ) {
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
