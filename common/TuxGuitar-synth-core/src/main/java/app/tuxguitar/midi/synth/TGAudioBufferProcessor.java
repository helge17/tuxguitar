package app.tuxguitar.midi.synth;

public class TGAudioBufferProcessor {

	private TGSynthModel synthesizer;
	private TGAudioBuffer buffer;

	public TGAudioBufferProcessor(TGSynthModel synthesizer) {
		this.synthesizer = synthesizer;
		this.buffer = new TGAudioBuffer();
	}

	// the next block: the synthesizer's listener sends its events first; each channel is delayed to the slowest chain
	public void process() {
		this.synthesizer.beforeBlock(TGAudioBuffer.BUFFER_SIZE / 2);
		this.buffer.clear();
		int latency = this.synthesizer.getLatency();
		for( int i = 0; i < this.synthesizer.countChannels(); i ++ ){
			TGSynthChannel channel = this.synthesizer.getChannel( i );
			if( channel != null ){
				channel.fillBuffer(this.buffer, latency - channel.getLatency());
			}
		}
		this.buffer.clip();
	}

	public TGAudioBuffer getBuffer() {
		return this.buffer;
	}
}
