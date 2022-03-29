package org.herac.tuxguitar.player.impl.midiport.lv2;

import org.herac.tuxguitar.midi.synth.TGAudioProcessor;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUI;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUICallback;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUIFactory;
import org.herac.tuxguitar.player.impl.midiport.lv2.ui.LV2AudioProcessorUI;
import org.herac.tuxguitar.util.TGContext;

public class LV2MidiProcessorUIFactory implements TGAudioProcessorUIFactory {
	
	private TGContext context;
	
	public LV2MidiProcessorUIFactory(TGContext context) {
		this.context = context;
	}
	
	public String getType() {
		return "LV2";
	}
	
	public TGAudioProcessorUI create(TGAudioProcessor processor, TGAudioProcessorUICallback callback) {
		return new LV2AudioProcessorUI(this.context, (LV2AudioProcessorWrapper) processor, callback);
	}
}
