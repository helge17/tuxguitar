package org.herac.tuxguitar.player.impl.midiport.lv2;

import org.herac.tuxguitar.midi.synth.TGMidiProcessor;
import org.herac.tuxguitar.midi.synth.TGMidiProcessorFactory;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2World;

public class LV2MidiProcessorFactory implements TGMidiProcessorFactory {
	
	private LV2World world;
	
	public LV2MidiProcessorFactory(LV2World world) {
		this.world = world;
	}
	
	public String getType() {
		return "LV2";
	}
	
	public TGMidiProcessor createProcessor() {
		try {
			return new LV2MidiProcessor(this.world);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
