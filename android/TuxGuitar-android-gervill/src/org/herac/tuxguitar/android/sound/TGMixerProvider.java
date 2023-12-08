package org.herac.tuxguitar.android.sound;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.spi.MixerProvider;

public class TGMixerProvider extends MixerProvider {
	
	@Override
	public Info[] getMixerInfo() {
		return new Info[] { TGMixer.MIXER_INFO };
	}

	@Override
	public Mixer getMixer(Info info) {
		if( TGMixer.MIXER_INFO.equals(info) ) {
			return new TGMixer();
		}
		return null;
	}
}
