package app.tuxguitar.midi.synth.impl;

import app.tuxguitar.midi.synth.TGMidiProcessor;
import app.tuxguitar.midi.synth.TGMidiProcessorFactory;
import app.tuxguitar.util.TGContext;

public class GervillProcessorFactory implements TGMidiProcessorFactory {

	public static final String TYPE = "gervill";

	private TGContext context;

	public GervillProcessorFactory(TGContext context) {
		this.context = context;
	}

	public String getType() {
		return TYPE;
	}

	public TGMidiProcessor createProcessor() {
		return new GervillProcessor(this.context);
	}
}
