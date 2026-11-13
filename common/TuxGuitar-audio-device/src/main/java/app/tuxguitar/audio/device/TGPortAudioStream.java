package app.tuxguitar.audio.device;

/**
 * A PortAudio stream: the native side copies each block's input into the input buffer, calls callback(frames) on the
 * device's thread, then plays the output buffer (see TGPlanarBlocks).
 */
final class TGPortAudioStream implements TGAudioStream {

	private final TGAudioCallback callback;
	private final int block;
	private final TGPlanarBlocks input;
	private final TGPlanarBlocks output;
	private final long handle;
	// fixed once open, so they can be read after closing
	private final int sampleRate;
	private final int inputLatency;
	private final int outputLatency;
	private boolean closed;

	interface Opener {
		long open(TGPortAudioStream stream) throws TGAudioDeviceException;
	}

	TGPortAudioStream(int inputChannels, int outputChannels, int block, TGAudioCallback callback, Opener opener) throws TGAudioDeviceException {
		this.callback = callback;
		this.block = block;
		this.input = new TGPlanarBlocks(inputChannels, block);
		this.output = new TGPlanarBlocks(outputChannels, block);
		this.handle = TGPortAudio.check(opener.open(this));
		double rate = TGPortAudio.nSampleRate(this.handle);
		this.sampleRate = (int) Math.round(rate);
		this.inputLatency = (int) Math.round(TGPortAudio.nInputLatency(this.handle) * rate);
		this.outputLatency = (int) Math.round(TGPortAudio.nOutputLatency(this.handle) * rate);
	}

	TGPlanarBlocks getInput() {
		return this.input;
	}

	TGPlanarBlocks getOutput() {
		return this.output;
	}

	// called by the native side on the device's thread
	void callback(int frames) {
		this.input.read(frames);
		this.callback.process(this.input.getChannels(), this.output.getChannels(), frames);
		this.output.write(frames);
	}

	public int getSampleRate() {
		return this.sampleRate;
	}

	public int getBlock() {
		return this.block;
	}

	public int getInputLatency() {
		return this.inputLatency;
	}

	public int getOutputLatency() {
		return this.outputLatency;
	}

	public void start() throws TGAudioDeviceException {
		TGPortAudio.check(TGPortAudio.nStart(this.handle));
	}

	public synchronized void close() {
		if (!this.closed) {
			this.closed = true;
			TGPortAudio.nClose(this.handle);
		}
	}
}
