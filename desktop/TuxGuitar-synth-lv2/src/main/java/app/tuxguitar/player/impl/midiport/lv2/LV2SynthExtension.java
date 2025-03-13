package app.tuxguitar.player.impl.midiport.lv2;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.midi.synth.TGAudioProcessorFactory;
import app.tuxguitar.midi.synth.TGMidiProcessorFactory;
import app.tuxguitar.midi.synth.TGSynthExtension;
import app.tuxguitar.midi.synth.TGSynthExtensionPlugin;
import app.tuxguitar.midi.synth.ui.TGAudioProcessorUIFactory;
import app.tuxguitar.player.impl.midiport.lv2.jni.LV2World;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class LV2SynthExtension extends TGSynthExtensionPlugin {

	public LV2SynthExtension() {
		super();
	}

	public String getModuleId() {
		return LV2Module.MODULE_ID;
	}

	@Override
	public List<TGSynthExtension<?>> createExtensions(TGContext context) throws TGPluginException {
		List<TGSynthExtension<?>> extensions = new ArrayList<TGSynthExtension<?>>();
		try {
			LV2World lv2World = new LV2World();
			lv2World.getPlugins();

			extensions.add(new TGSynthExtension<TGMidiProcessorFactory>(TGMidiProcessorFactory.class, new LV2MidiProcessorFactory(context, lv2World)));
			extensions.add(new TGSynthExtension<TGAudioProcessorFactory>(TGAudioProcessorFactory.class, new LV2AudioProcessorFactory(context, lv2World)));
			extensions.add(new TGSynthExtension<TGAudioProcessorUIFactory>(TGAudioProcessorUIFactory.class, new LV2MidiProcessorUIFactory(context)));
			extensions.add(new TGSynthExtension<TGAudioProcessorUIFactory>(TGAudioProcessorUIFactory.class, new LV2AudioProcessorUIFactory(context)));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return extensions;
	}
}