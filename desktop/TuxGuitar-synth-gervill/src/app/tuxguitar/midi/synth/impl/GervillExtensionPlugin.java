package app.tuxguitar.midi.synth.impl;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.midi.synth.TGMidiProcessorFactory;
import app.tuxguitar.midi.synth.TGSynthExtension;
import app.tuxguitar.midi.synth.TGSynthExtensionPlugin;
import app.tuxguitar.midi.synth.ui.TGAudioProcessorUIFactory;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class GervillExtensionPlugin extends TGSynthExtensionPlugin {

	public static final String MODULE_ID = "tuxguitar-synth-gervill";

	public String getModuleId() {
		return MODULE_ID;
	}

	@Override
	public List<TGSynthExtension<?>> createExtensions(TGContext context) throws TGPluginException {
		List<TGSynthExtension<?>> extensions = new ArrayList<TGSynthExtension<?>>();
		extensions.add(new TGSynthExtension<TGMidiProcessorFactory>(TGMidiProcessorFactory.class, new GervillProcessorFactory(context)));
		extensions.add(new TGSynthExtension<TGAudioProcessorUIFactory>(TGAudioProcessorUIFactory.class, new GervillProcessorUIFactory(context)));
		return extensions;
	}
}
