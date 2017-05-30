package org.herac.tuxguitar.midi.synth.impl;

import org.herac.tuxguitar.midi.synth.TGAudioProcessorFactoryCallback;
import org.herac.tuxguitar.midi.synth.TGMidiProcessor;
import org.herac.tuxguitar.midi.synth.TGMidiProcessorFactory;
import org.herac.tuxguitar.util.TGContext;

public class GervillProcessorFactory implements TGMidiProcessorFactory {
	
	public static final String TYPE = "gervill";
	
	private TGContext context;
	
	public GervillProcessorFactory(TGContext context) {
		this.context = context;
	}
	
	public String getType() {
		return TYPE;
	}
	
	public void createProcessor(TGAudioProcessorFactoryCallback<TGMidiProcessor> callback) {
		callback.onCreate(new GervillProcessor(this.context));
	}
}
