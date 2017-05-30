package org.herac.tuxguitar.midi.synth;

public class TGAudioBufferProcessor implements Runnable {
	
	private boolean running;
	private boolean finished;
	private Object lock;
	private TGSynthesizer synthesizer;
	private TGAudioBuffer buffer;
	
	public TGAudioBufferProcessor(TGSynthesizer synthesizer) {
		this.synthesizer = synthesizer;
		this.buffer = new TGAudioBuffer();
		this.lock = new Object();
		this.finished = true;
	}
	
	public void process(TGAudioBuffer target) {
		synchronized (this.lock) {
			target.write(this.buffer);
			
			this.lock.notifyAll();
		}
	}
	
	public void run() {
		this.finished = false;
		while ( this.isRunning() ) {
			synchronized (this.lock) {
				try {
					this.buffer.clear();
					for( int i = 0; i < this.synthesizer.countChannels() ; i ++ ){
						TGSynthChannel channel = this.synthesizer.getChannel( i );
						if( channel != null ){
							channel.fillBuffer(this.buffer);
						}
					}
					this.buffer.clip();
					this.lock.wait();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		this.finished = true;
	}
	
	private void waitThread(){
		while(!this.finished ){
			synchronized (this.lock) {
				this.lock.notifyAll();
			}
			Thread.yield();
		}
	}
	
	private void startThread(){
		Thread thread = new Thread( this );
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
