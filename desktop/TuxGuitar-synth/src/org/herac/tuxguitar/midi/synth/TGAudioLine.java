package org.herac.tuxguitar.midi.synth;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.herac.tuxguitar.util.TGException;

public class TGAudioLine {
	
	public static final AudioFormat AUDIO_FORMAT = new AudioFormat(TGAudioBuffer.SAMPLE_RATE, 16, TGAudioBuffer.CHANNELS, true, TGAudioBuffer.BIGENDIAN);
	
	private SourceDataLine line;
	
	public TGAudioLine(TGSynthesizer synthesizer) {
		try {
			this.line = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, AUDIO_FORMAT));
			this.line.open(AUDIO_FORMAT, (TGAudioBuffer.CHANNELS * TGAudioBuffer.BUFFER_SIZE) * Math.max(synthesizer.getSettings().getAudioBufferSize(), 1));
			this.line.start();
		} catch (Throwable e) {
			throw new TGException(e);
		}
	}
	
	public void write(TGAudioBuffer buffer) {
		this.line.write(buffer.getBuffer(), 0, buffer.getLength());
	}
}
