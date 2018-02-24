package org.herac.tuxguitar.player.impl.midiport.lv2;

import org.herac.tuxguitar.midi.synth.TGAudioProcessor;
import org.herac.tuxguitar.midi.synth.TGAudioProcessorFactory;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2World;

public class LV2AudioProcessorFactory implements TGAudioProcessorFactory {
	
	private LV2World world;
	
	public LV2AudioProcessorFactory(LV2World world) {
		this.world = world;
	}
	
	public String getType() {
		return "LV2";
	}
	
	public TGAudioProcessor createProcessor() {
		try {
			return new LV2AudioProcessorWrapper(this.world);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
