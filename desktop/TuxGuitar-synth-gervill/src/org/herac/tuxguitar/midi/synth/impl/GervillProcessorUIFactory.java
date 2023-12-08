package org.herac.tuxguitar.midi.synth.impl;

import org.herac.tuxguitar.midi.synth.TGAudioProcessor;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUI;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUICallback;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUIFactory;
import org.herac.tuxguitar.util.TGContext;

public class GervillProcessorUIFactory implements TGAudioProcessorUIFactory {
	
	public static final String TYPE = "gervill";
	
	private TGContext context;
	
	public GervillProcessorUIFactory(TGContext context) {
		this.context = context;
	}
	
	public String getType() {
		return TYPE;
	}
	
	public TGAudioProcessorUI create(TGAudioProcessor processor, TGAudioProcessorUICallback callback) {
		return new GervillProcessorUI(this.context, (GervillProcessor) processor, callback);
	}
}
