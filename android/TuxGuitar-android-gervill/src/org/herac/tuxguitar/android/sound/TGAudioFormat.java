package org.herac.tuxguitar.android.sound;

import javax.sound.sampled.AudioFormat;

public class TGAudioFormat {
	
	private AudioFormat source;
	
	public TGAudioFormat(AudioFormat source) {
		this.source = source;
	}

	public AudioFormat getSource() {
		return this.source;
	}

	public int getSampleRateInHz() {
		return Math.round(this.source.getSampleRate());
	}

	public int getChannelConfig() {
		if( this.source.getChannels() == 1 ) {
			return android.media.AudioFormat.CHANNEL_OUT_MONO;
		}
		if( this.source.getChannels() == 2 ) {
			return android.media.AudioFormat.CHANNEL_OUT_STEREO;
		}
		return android.media.AudioFormat.CHANNEL_OUT_DEFAULT;
	}

	public int getAudioFormat() {
		if( AudioFormat.Encoding.PCM_SIGNED.equals(this.source.getEncoding()) ) {
			return android.media.AudioFormat.ENCODING_PCM_16BIT;
		}
		if( AudioFormat.Encoding.PCM_UNSIGNED.equals(this.source.getEncoding()) ) {
			return android.media.AudioFormat.ENCODING_PCM_8BIT;
		}
		return android.media.AudioFormat.ENCODING_DEFAULT;
	}
}
