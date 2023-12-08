package org.herac.tuxguitar.player.base;

import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGVelocities;

public class MidiPlayerCountDown {
	
	public static final int DEFAULT_TEMPO_PERCENT = 100;
	
	private MidiPlayer player;
	private boolean enabled;
	private boolean running;
	private int tempoPercent;
	
	public MidiPlayerCountDown(MidiPlayer player){
		this.player = player;
		this.enabled = false;
		this.tempoPercent = DEFAULT_TEMPO_PERCENT;
	}
	
	public void start(){
		try {
			this.running = true;
			
			int channelId = this.player.getPercussionChannelId();
			if( channelId >= 0 ){
				TGMeasureHeader header = this.findMeasureHeader();
				if( header != null ){
					Object timerLock = new Object();
					
					int  tgTempo = ((header.getTempo().getValue() * this.getTempoPercent()) / DEFAULT_TEMPO_PERCENT);
					long tgLength = header.getTimeSignature().getDenominator().getTime();
					long tickLength = (long)(1000.00 * (60.00 / tgTempo * tgLength) / TGDuration.QUARTER_TIME);
					long tickStart = System.currentTimeMillis();
					
					int tickIndex = 0;
					int tickCount = header.getTimeSignature().getNumerator();
					
					while( this.isRunning() && tickIndex <= tickCount ){
							long currentTime = System.currentTimeMillis();
							if( tickStart <= currentTime ){
								tickStart += tickLength;
								tickIndex ++;
								if( tickIndex <= tickCount ){
									this.player.getOutputTransmitter().sendNoteOn(channelId, 37, TGVelocities.DEFAULT, -1, false);
									synchronized (timerLock) {
										timerLock.wait( 1 );
									}
									this.player.getOutputTransmitter().sendNoteOff(channelId, 37, TGVelocities.DEFAULT, -1, false);
								}
							}
						synchronized (timerLock) {
							timerLock.wait( 10 );
						}
					}
				}
			}
			
			this.running = false;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public TGMeasureHeader findMeasureHeader(){
		long tick = this.player.getTickPosition();
		long startPoint = this.player.getLoopSPosition();
		long start = startPoint;
		long length = 0;
		
		TGSong song = this.player.getSong();
		MidiRepeatController controller = new MidiRepeatController(song, this.player.getLoopSHeader() , this.player.getLoopEHeader() );
		while(!controller.finished()){
			TGMeasureHeader header = song.getMeasureHeader(controller.getIndex());
			controller.process();
			if(controller.shouldPlay()){
				
				start += length;
				length = header.getLength();
				
				//verifico si es el compas correcto
				if(tick >= start && tick < (start + length )){
					return header;
				}
			}
		}
		return null;
	}
	
	public boolean isRunning() {
		return (this.enabled && this.running && this.player.isRunning() && !this.player.isChangeTickPosition());
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getTempoPercent() {
		return tempoPercent;
	}

	public void setTempoPercent(int tempoPercent) {
		this.tempoPercent = tempoPercent;
	}
}
