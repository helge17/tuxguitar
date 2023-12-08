package org.herac.tuxguitar.player.impl.midiport.lv2;

import org.herac.tuxguitar.midi.synth.TGAudioProcessor;
import org.herac.tuxguitar.midi.synth.TGAudioProcessorFactory;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2World;
import org.herac.tuxguitar.util.TGContext;

public class LV2AudioProcessorFactory implements TGAudioProcessorFactory {
	
	private TGContext context;
	private LV2World world;
	
	public LV2AudioProcessorFactory(TGContext context, LV2World world) {
		this.context = context;
		this.world = world;
	}
	
	public String getType() {
		return LV2Module.AUDIO_TYPE;
	}
	
	public TGAudioProcessor createProcessor() {
		try {
			return new LV2AudioProcessorWrapper(this.context, this.world);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
