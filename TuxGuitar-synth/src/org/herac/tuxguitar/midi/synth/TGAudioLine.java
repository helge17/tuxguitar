package org.herac.tuxguitar.midi.synth;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class TGAudioLine {
	
	public static final int CHANNELS = 2;
	public static final int BUFFER_SIZE = 2048;
	public static final float SAMPLE_RATE = 44100f;
	public static final boolean BIGENDIAN = false;
	public static final AudioFormat AUDIO_FORMAT = new AudioFormat( SAMPLE_RATE, 16, CHANNELS, true, BIGENDIAN );
	
	private boolean open;
	private TGAudioBuffer buffer;
	private SourceDataLine line;
	
	public TGAudioLine() {
		try {
			this.open = false;
			this.buffer = new TGAudioBuffer();
			
			this.line = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, AUDIO_FORMAT));
			this.line.open(AUDIO_FORMAT, this.buffer.getLength());
			this.line.start();
			
			this.open = true;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public TGAudioBuffer getBuffer(){
		return this.buffer;
	}
	
	public void reset(){
		this.buffer.clear();
	}
	
	public boolean write() {
		if( this.open ){
			this.line.write(this.buffer.getBuffer(), 0, this.buffer.getLength());
			
			return true;
		}
		return false;
	}
}
