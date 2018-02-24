package org.herac.tuxguitar.player.impl.midiport.lv2;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.midi.synth.TGAudioProcessorFactory;
import org.herac.tuxguitar.midi.synth.TGMidiProcessorFactory;
import org.herac.tuxguitar.midi.synth.TGSynthExtension;
import org.herac.tuxguitar.midi.synth.TGSynthExtensionPlugin;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUIFactory;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2World;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class LV2SynthExtension extends TGSynthExtensionPlugin {

	private LV2World world;
	
	public LV2SynthExtension() {
		this.world = new LV2World();
	}
	
	public String getModuleId() {
		return LV2Module.MODULE_ID;
	}
	
	@Override
	public List<TGSynthExtension<?>> createExtensions(TGContext context) throws TGPluginException {
		List<TGSynthExtension<?>> extensions = new ArrayList<TGSynthExtension<?>>();
		extensions.add(new TGSynthExtension<TGMidiProcessorFactory>(TGMidiProcessorFactory.class, new LV2MidiProcessorFactory(this.world)));
		extensions.add(new TGSynthExtension<TGAudioProcessorFactory>(TGAudioProcessorFactory.class, new LV2AudioProcessorFactory(this.world)));
		extensions.add(new TGSynthExtension<TGAudioProcessorUIFactory>(TGAudioProcessorUIFactory.class, new LV2MidiProcessorUIFactory(context)));
		extensions.add(new TGSynthExtension<TGAudioProcessorUIFactory>(TGAudioProcessorUIFactory.class, new LV2AudioProcessorUIFactory(context)));
		
		return extensions;
	}
}