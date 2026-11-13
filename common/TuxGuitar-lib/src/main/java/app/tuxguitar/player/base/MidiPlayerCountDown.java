package app.tuxguitar.player.base;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGVelocities;

public class MidiPlayerCountDown {

	public static final int DEFAULT_TEMPO_PERCENT = 100;

	private MidiPlayer player;
	private boolean enabled;
	private boolean running;
	private int tempoPercent;

	private int tickCount;

	// nothing notifies it: waiting on it only lets time pass
	private final Object timerLock = new Object();

	public MidiPlayerCountDown(MidiPlayer player){
		this.player = player;
		this.enabled = false;
		this.tempoPercent = DEFAULT_TEMPO_PERCENT;
		this.tickCount = 0;
	}

	public void start(){
		try {
			this.running = true;

			int channelId = this.player.getPercussionChannelId();
			if( channelId >= 0 ){
				TGMeasureHeader header = this.findMeasureHeader();
				if( header != null ){
					this.playTicks(channelId, header);
				}
			}

			this.running = false;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	// one tick per beat of the header's time signature (unless a tick count is set), at the header's tempo scaled
	// exactly by the tempo percent
	private void playTicks(int channelId, TGMeasureHeader header) throws MidiPlayerException, InterruptedException {
		double tgTempo = ((header.getTempo().getQuarterValue() * (double) this.getTempoPercent()) / DEFAULT_TEMPO_PERCENT);
		long tgLength = header.getTimeSignature().getDenominator().getTime();
		long tickLength = (long)(1000.00 * (60.00 / tgTempo * tgLength) / TGDuration.QUARTER_TIME);
		long tickStart = this.currentTime();

		int tickIndex = 0;

		int localTickCount = (this.tickCount != 0) ? this.tickCount : header.getTimeSignature().getNumerator();
		while( this.isRunning() && tickIndex <= localTickCount ){
			if( tickStart <= this.currentTime() ){
				tickStart += tickLength;
				tickIndex ++;
				if( tickIndex <= localTickCount ){
					this.player.getOutputTransmitter().sendNoteOn(channelId, 37, TGVelocities.DEFAULT, -1, false);
					this.pause( 1 );
					this.player.getOutputTransmitter().sendNoteOff(channelId, 37, TGVelocities.DEFAULT, -1, false);
				}
			}
			this.pause( 10 );
		}
	}

	// the clock the ticks are timed by, in ms
	protected long currentTime(){
		return System.currentTimeMillis();
	}

	// lets the given time (ms) pass
	protected void pause(long millis) throws InterruptedException {
		synchronized (this.timerLock) {
			this.timerLock.wait( millis );
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

	public void setTickCount(int ticks) {
		this.tickCount = ticks;
	}
	
	public int getTickCount() {
		return this.tickCount;
	}
	
	public void setTempoPercent(int tempoPercent) {
		this.tempoPercent = tempoPercent;
	}
}
