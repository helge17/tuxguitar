package org.herac.tuxguitar.player.impl.sequencer;

import org.herac.tuxguitar.song.models.TGDuration;

public class MidiTickPlayer {
	
	private static final long SECOND_IN_NANOS = (1000 * 1000000);
	
	private int tempo;
	private long tick;
	private long time;
	private long lastTime;
	private long tickLength;
	private boolean tickChanged;
	
	public MidiTickPlayer(){
		super();
	}
	
	public void process() {
		this.lastTime = this.time;
		this.time = System.nanoTime();
		if(!this.tickChanged){
			this.tick += Math.round((double) TGDuration.QUARTER_TIME * ((double)getTempo() * (double)(this.time - this.lastTime) / 60.00) / (double) SECOND_IN_NANOS);
		}
		this.tickChanged = false;
	}
	
	public void clearTick(){
		this.tickLength = 0;
	}
	
	public int getTempo() {
		return this.tempo;
	}
	
	public void setTempo(int tempo) {
		this.tempo = tempo;
	}
	
	public long getTick() {
		return this.tick;
	}
	
	public void setTick(long tick) {
		this.tick = tick;
		this.tickChanged = true;
	}
	
	public long getTickLength() {
		return this.tickLength;
	}
	
	public void notifyTick(long tick){
		this.tickLength = Math.max(this.tickLength, tick);
	}
}
