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
		double framePos = 0;
		double tempo = 120;
		double tick = TGDuration.QUARTER_TIME;
		
		List tempoChanges = this.sequencer.getJackEventController().getTempoChanges();
		for(int i = 0; i < tempoChanges.size(); i ++){
			long[] tc = (long[])tempoChanges.get(i);
			double tcTick = tc[0];
			double tcValue = tc[1];
			
			double tickFrames = ( (((double)frameRate * (tcTick - tick)) / (double)TGDuration.QUARTER_TIME ) * (60.00 / tempo) );
			if( framePos + tickFrames <= frame ){
				framePos += tickFrames;
				tempo = tcValue;
				tick = tcTick;
			}else{
				break;
			}
		}
		if( frame > framePos ){
			double timeFrame = ( ( (double)(frame - framePos) / (double)frameRate ) * 1000.00 );
			double timeTick =  ( ( timeFrame * (double)TGDuration.QUARTER_TIME ) / 1000.00 );
			
			tick += ( timeTick * ( tempo / 60.00 ) );
		}
		
		return tick;
	}
	
	public double tickToFrame( long tick , long frameRate ){
		double tickPos = TGDuration.QUARTER_TIME;
		double tempo = 120;
		double frame = 0;
		
		List tempoChanges = this.sequencer.getJackEventController().getTempoChanges();
		for(int i = 0; i < tempoChanges.size(); i ++){
			long[] tc = (long[])tempoChanges.get(i);
			double tcTick = tc[0];
			double tcValue = tc[1];
			
			double tickFrames = ((((double) frameRate * (tcTick - tickPos)) / (double)TGDuration.QUARTER_TIME ) * (60.00 / tempo));
			if( tcTick <= tick ){
				frame += tickFrames;
				tempo = tcValue;
				tickPos = tcTick;
			}else{
				break;
			}
		}
		if( tick > tickPos ){
			double timeTick = ( ( (double)( tick - tickPos ) / (double)TGDuration.QUARTER_TIME ) * 1000.00 );
			double timeFrame = ( ( timeTick * (double)frameRate ) / 1000.00 );
			
			frame += (timeFrame * ( 60.00 / tempo));
		}
		
		return frame;
	}
}
