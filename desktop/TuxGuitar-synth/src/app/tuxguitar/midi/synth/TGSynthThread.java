package app.tuxguitar.midi.synth;

// the synthesizer's own output: a thread rendering it into a Java Sound line
public class TGSynthThread implements Runnable, TGSynthOutput {

	private boolean running;
	private boolean finished;
	private TGSynthesizer synthesizer;

	public TGSynthThread(TGSynthesizer synthesizer) {
		this.synthesizer = synthesizer;
		this.finished = true;
	}

	public boolean play(TGSynthModel played) {
		this.start();
		return true;
	}

	public void stop(TGSynthModel stopped) {
		this.stop();
	}

	public void run() {
		try {
			this.finished = false;

			TGAudioSync audioSync = new TGAudioSync();
			TGAudioLine audioLine = new TGAudioLine(this.synthesizer);
			TGAudioBufferProcessor audioProcessor = new TGAudioBufferProcessor(this.synthesizer);

			while(this.isRunning()) {
				audioProcessor.process();
				audioLine.write(audioProcessor.getBuffer());
				audioSync.sync();
			}
		} finally {
			this.finished = true;
		}
	}

	private void startThread(){
		Thread thread = new Thread(this);
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.setDaemon(true);
		thread.start();
	}

	private void waitThread(){
		while(!this.finished ){
			Thread.yield();
		}
	}

	public void start(){
		if( this.finished ) {
			this.running = true;
			this.startThread();
		}
	}

	public void stop(){
		if(!this.finished ) {
			this.running = false;
			this.waitThread();
		}
	}

	public boolean isRunning(){
		return this.running;
	}
}
