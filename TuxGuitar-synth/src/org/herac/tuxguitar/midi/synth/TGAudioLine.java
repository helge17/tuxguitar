package org.herac.tuxguitar.midi.synth;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.herac.tuxguitar.util.TGException;

public class TGAudioLine implements Runnable {
	
	public static final AudioFormat AUDIO_FORMAT = new AudioFormat(TGAudioBuffer.SAMPLE_RATE, 16, TGAudioBuffer.CHANNELS, true, TGAudioBuffer.BIGENDIAN);
	
	private boolean running;
	private boolean finished;
	private int currentBuffer;
	private TGAudioBuffer[] buffers;
	private SourceDataLine line;
	private Object lock;
	
	public TGAudioLine() {
		try {
			this.lock = new Object();
			this.buffers = new TGAudioBuffer[] {new TGAudioBuffer(), new TGAudioBuffer()};
			
			this.line = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, AUDIO_FORMAT));
			this.line.open(AUDIO_FORMAT, TGAudioBuffer.CHANNELS * TGAudioBuffer.BUFFER_SIZE);
			this.line.start();
			
			this.finished = true;
		} catch (Throwable e) {
			throw new TGException(e);
		}
	}
	
	public void write(TGAudioBuffer buffer) {
		int currentBuffer = -1;
		
		synchronized (this.lock) {
			currentBuffer = this.currentBuffer;
			
			this.buffers[(currentBuffer + 1) % this.buffers.length].write(buffer);
		}
		
		while( this.currentBuffer == currentBuffer ) {
			Thread.yield();
		}
	}
	
	public void run() {
		this.finished = false;
		
		while ( this.isRunning() ) {
			TGAudioBuffer buffer = this.buffers[this.currentBuffer];
			
			this.line.write(buffer.getBuffer(), 0, buffer.getLength());
			
			synchronized (this.lock) {
				this.currentBuffer = ((this.currentBuffer + 1) % this.buffers.length);
			}
		}
		
		this.finished = true;
	}
	
	private void waitThread(){
		while(!this.finished ){
			Thread.yield();
		}
	}
	
	private void startThread(){
		Thread thread = new Thread( this );
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.setDaemon(true);
		thread.start();
	}
	
	public void start(){
		if( this.finished ){
			this.running = true;
			this.startThread();
		}
	}
	
	public void stop(){
		if(!this.finished ){
			this.running = false;
			this.waitThread();
		}
	}
	
	public boolean isRunning(){
		return this.running;
	}
}
