package org.herac.tuxguitar.player.impl.midiport.vst;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.midi.synth.TGAudioProcessorFactory;
import org.herac.tuxguitar.midi.synth.TGMidiProcessorFactory;
import org.herac.tuxguitar.midi.synth.TGSynthExtension;
import org.herac.tuxguitar.midi.synth.TGSynthExtensionPlugin;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUIFactory;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class VSTSynthExtension extends TGSynthExtensionPlugin {

	public String getModuleId() {
		return VSTModule.MODULE_ID;
	}
	
	@Override
	public List<TGSynthExtension<?>> createExtensions(TGContext context) throws TGPluginException {
		List<TGSynthExtension<?>> extensions = new ArrayList<TGSynthExtension<?>>();
		extensions.add(new TGSynthExtension<TGMidiProcessorFactory>(TGMidiProcessorFactory.class, new VSTMidiProcessorFactory()));
		extensions.add(new TGSynthExtension<TGAudioProcessorFactory>(TGAudioProcessorFactory.class, new VSTAudioProcessorFactory()));
		extensions.add(new TGSynthExtension<TGAudioProcessorUIFactory>(TGAudioProcessorUIFactory.class, new VSTMidiProcessorUIFactory(context)));
		extensions.add(new TGSynthExtension<TGAudioProcessorUIFactory>(TGAudioProcessorUIFactory.class, new VSTAudioProcessorUIFactory(context)));
		
		return extensions;
	}
}