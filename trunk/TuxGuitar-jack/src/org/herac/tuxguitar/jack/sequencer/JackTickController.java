package org.herac.tuxguitar.jack.sequencer;

import java.util.List;

import org.herac.tuxguitar.song.models.TGDuration;

public class JackTickController {
	
	private int tempo;
	private long frame;
	private long lastFrame;
	private long tickLength;
	private double tick;
	
	private Object lock;
	private JackSequencer sequencer;
	
	public JackTickController(JackSequencer sequencer){
		this.lock = new Object();
		this.sequencer = sequencer;
	}
	
	public void process() {
		synchronized (this.lock) {
			long frameRate = this.sequencer.getJackClient().getTransportFrameRate();
			this.lastFrame = this.frame;
			this.frame = this.sequencer.getJackClient().getTransportFrame();
			this.tick += ((double)TGDuration.QUARTER_TIME * ((double)getTempo() * (double)(this.frame - this.lastFrame) / 60.00) / (double)frameRate);
		}
	}
	
	public void setTick(long tick , boolean updateTransport ) {
		synchronized (this.lock) {
			long frameRate = this.sequencer.getJackClient().getTransportFrameRate();
			if( updateTransport ){
				this.sequencer.getJackClient().setTransportFrame( Math.round( tickToFrame(tick, frameRate )) );
			}
			this.frame = this.sequencer.getJackClient().getTransportFrame();
			this.tick = this.frameToTick( this.frame , frameRate );
		}
	}
	
	public double getTick() {
		return this.tick;
	}
	
	public long getTickLength() {
		return this.tickLength;
	}
	
	public void clearTick(){
		this.tickLength = 0;
	}
	
	public void notifyTick(long tick){
		this.tickLength = Math.max(this.tickLength,tick);
	}
	
	public void setTempo(int tempo) {
		this.tempo = tempo;
	}
	
	public int getTempo() {
		return this.tempo;
	}
	
	public double frameToTick( long frame , long frameRate ){
		int tempo = 120;
		int timeInterval = 1;
		double timeFrame = ( ( (double)timeInterval / (double)frameRate ) * 1000.00 );
		double timeTick =  ( ( timeFrame * (double)TGDuration.QUARTER_TIME ) / 1000.00 );
		double tick = TGDuration.QUARTER_TIME;
		List tempoChanges = this.sequencer.getJackEventController().getTempoChanges();
		
		for( long frame_pos = 0 ; frame_pos < frame ; frame_pos += timeInterval ){
			for(int i = 0; i < tempoChanges.size(); i ++){
				long[] tempoChange = (long[])tempoChanges.get(i);
				if( tempoChange[0] >= TGDuration.QUARTER_TIME && tempoChange[0] <= tick ){
					tempoChange[0] = -1;
					tempo = (int)tempoChange[1];
				}
			}
			
			tick += (timeTick * ((double)tempo / 60.00) );
		}
		
		return tick;
	}
	
	public double tickToFrame( long tick , long frameRate ){
		int tempo = 120;
		int timeInterval = 1;
		double timeTick = ( ( (double)timeInterval / (double)TGDuration.QUARTER_TIME ) * 1000.00 );
		double timeFrame = ( ( timeTick * (double)frameRate ) / 1000.00 );
		double frame = 0;
		List tempoChanges = this.sequencer.getJackEventController().getTempoChanges();
		
		for( long tick_pos = TGDuration.QUARTER_TIME ; tick_pos < tick ; tick_pos += timeInterval ){
			for(int i = 0; i < tempoChanges.size(); i ++){
				long[] tempoChange = (long[])tempoChanges.get(i);
				if( tempoChange[0] >= TGDuration.QUARTER_TIME && tempoChange[0] <= tick_pos ){
					tempoChange[0] = -1;
					tempo = (int)tempoChange[1];
				}
			}
			frame += (timeFrame * ( 60.00 / (double)tempo));
		}
		
		return frame;
	}
}
