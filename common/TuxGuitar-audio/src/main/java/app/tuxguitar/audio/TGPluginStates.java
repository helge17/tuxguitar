package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.midi.synth.TGAudioProcessor;
import app.tuxguitar.midi.synth.TGAudioProcessorFactory;
import app.tuxguitar.midi.synth.TGMidiProcessorFactory;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.vst3.TGVst3Element;
import app.tuxguitar.vst3.TGVst3Exception;
import app.tuxguitar.vst3.TGVst3Instance;

/**
 * Before a song is saved, each VST3 plugin of each sound chain gives its state, which the chain keeps in place of the
 * parameters set since the last one: saved with the song, it comes back byte for byte. An enabled plugin that runs (see
 * TGLivePlugins) gives the state it has as it runs; any other opens for it in a helper of its own, with what the chain
 * holds, and closes again (a disabled effect gives its state too). A channel without VST3 plugins is left as it is.
 */
final class TGPluginStates {

	private TGPluginStates() {
	}

	static void store(TGContext context, TGSong song, TGFactory factory, float sampleRate) {
		Iterator<TGChannel> channels = song.getChannels();
		while (channels.hasNext()) {
			TGChannel channel = channels.next();
			TGProgram chain = TGSoundChain.get(channel);
			List<TGProgramElement> plugins = plugins(chain);
			for (TGProgramElement plugin : plugins) {
				store(context, channel.getChannelId(), chain, plugin, sampleRate);
			}
			if (!plugins.isEmpty()) {
				TGSoundChain.save(channel, chain, factory);
			}
		}
	}

	// from the plugin as it runs when it is enabled, else (or when it is gone) from a fresh one
	private static void store(TGContext context, int channel, TGProgram chain, TGProgramElement plugin, float sampleRate) {
		TGVst3Instance running = (plugin.isEnabled() ? TGLivePlugins.getInstance(context).find(channel, plugin.getId()) : null);
		if (running == null || !store(running, plugin)) {
			store(processor(context, plugin, chain), plugin, sampleRate);
		}
	}

	// false when the plugin is gone
	private static boolean store(TGVst3Instance running, TGProgramElement plugin) {
		try {
			TGVst3Element.setState(plugin, running.getState());
			return true;
		} catch (TGVst3Exception e) {
			return false;
		}
	}

	// the chain's VST3 elements: its source, then its effects
	private static List<TGProgramElement> plugins(TGProgram chain) {
		List<TGProgramElement> plugins = new ArrayList<TGProgramElement>();
		plugins.add(chain.getReceiver());
		for (int i = 0; i < chain.countOutputs(); i++) {
			plugins.add(chain.getOutput(i));
		}
		plugins.removeIf(element -> !TGVst3Element.isVst3(element));
		return plugins;
	}

	// a new processor of the element: the source's from the MIDI processor factories, an effect's from the audio ones
	static TGAudioProcessor processor(TGContext context, TGProgramElement plugin, TGProgram chain) {
		TGSynthManager manager = TGSynthManager.getInstance(context);
		if (plugin == chain.getReceiver()) {
			TGMidiProcessorFactory factory = manager.findMidiProcessorFactory(plugin.getType());
			return factory.createProcessor();
		}
		TGAudioProcessorFactory factory = manager.findAudioProcessorFactory(plugin.getType());
		return factory.createProcessor();
	}

	private static void store(TGAudioProcessor processor, TGProgramElement plugin, float sampleRate) {
		try {
			processor.setSampleRate(sampleRate);
			processor.restoreParameters(plugin.getParameters());
			processor.storeParameters(plugin.getParameters());
		} finally {
			processor.close();
		}
	}
}
