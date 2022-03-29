package org.herac.tuxguitar.player.impl.midiport.lv2;

import org.herac.tuxguitar.midi.synth.TGMidiProcessor;
import org.herac.tuxguitar.midi.synth.TGMidiProcessorFactory;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2World;
import org.herac.tuxguitar.util.TGContext;

public class LV2MidiProcessorFactory implements TGMidiProcessorFactory {
	
	private TGContext context;
	private LV2World world;
	
	public LV2MidiProcessorFactory(TGContext context, LV2World world) {
		this.context = context;
		this.world = world;
	}
	
	public String getType() {
		return "LV2";
	}
	
	public TGMidiProcessor createProcessor() {
		try {
			return new LV2MidiProcessor(this.context, this.world);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
