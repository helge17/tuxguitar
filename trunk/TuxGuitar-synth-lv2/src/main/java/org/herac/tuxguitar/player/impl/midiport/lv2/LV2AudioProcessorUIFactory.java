package org.herac.tuxguitar.player.impl.midiport.lv2;

import org.herac.tuxguitar.midi.synth.TGAudioProcessor;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUI;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUICallback;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUIFactory;
import org.herac.tuxguitar.player.impl.midiport.lv2.ui.LV2AudioProcessorUI;
import org.herac.tuxguitar.util.TGContext;

public class LV2AudioProcessorUIFactory implements TGAudioProcessorUIFactory {
	
	private TGContext context;
	private LV2PluginValidator validator;
	
	public LV2AudioProcessorUIFactory(TGContext context) {
		this.context = context;
		this.validator = new LV2AudioPluginValidator();
	}
	
	public String getType() {
		return "LV2";
	}
	
	public TGAudioProcessorUI create(TGAudioProcessor processor, TGAudioProcessorUICallback callback) {
		return new LV2AudioProcessorUI(this.context, (LV2AudioProcessorWrapper) processor, this.validator, callback);
	}
}
