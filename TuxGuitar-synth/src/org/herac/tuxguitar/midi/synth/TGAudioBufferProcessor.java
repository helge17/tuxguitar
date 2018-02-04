package org.herac.tuxguitar.midi.synth;

public class TGAudioBufferProcessor {
	
	private TGSynthModel synthesizer;
	private TGAudioBuffer buffer;
	
	public TGAudioBufferProcessor(TGSynthModel synthesizer) {
		this.synthesizer = synthesizer;
		this.buffer = new TGAudioBuffer();
	}
	
	public void process() {
		this.buffer.clear();
		for( int i = 0; i < this.synthesizer.countChannels(); i ++ ){
			TGSynthChannel channel = this.synthesizer.getChannel( i );
			if( channel != null ){
				channel.fillBuffer(this.buffer);
			}
		}
		this.buffer.clip();
	}

	public TGAudioBuffer getBuffer() {
		return this.buffer;
	}
}
