package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.midi.synth.TGProgramPropertiesUtil;
import app.tuxguitar.midi.synth.TGSynthChannel;
import app.tuxguitar.midi.synth.TGSynthChannelProperties;
import app.tuxguitar.midi.synth.impl.GervillProcessorFactory;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGChannelParameter;
import app.tuxguitar.util.TGMessagesManager;
import app.tuxguitar.vst3.TGVst3Element;

/**
 * What a channel sounds through: its source (the General MIDI program the channel plays, a VST3 instrument, or an audio
 * channel's takes), followed by its effects, in processing order, each enabled or disabled. It is the TuxGuitar
 * Synthesizer's program of the channel, kept in the channel's parameters under the sound chain's keys (TGSynthChannel
 * plays them); a channel without one sounds the custom program older versions saved, else its source alone. The reading
 * names the source and the effects joined by " > ", a disabled effect in brackets.
 */
public final class TGSoundChain {

	// the source type of an audio channel's chain: its takes, rendered by the audio engine
	public static final String TAKES = "takes";

	private TGSoundChain() {
	}

	public static String read(TGChannel channel) {
		TGProgram chain = get(channel);
		StringJoiner reading = new StringJoiner(" > ");
		reading.add(name(chain.getReceiver(), channel));
		for (int i = 0; i < chain.countOutputs(); i++) {
			TGProgramElement effect = chain.getOutput(i);
			reading.add(effect.isEnabled() ? name(effect, channel) : "[" + name(effect, channel) + "]");
		}
		return reading.toString();
	}

	// a General MIDI source is read by the channel's program; any other element by its plugin's name, else its type
	private static String name(TGProgramElement element, TGChannel channel) {
		if (TGVst3Element.isVst3(element)) {
			return TGVst3Element.getName(element);
		}
		if (GervillProcessorFactory.TYPE.equals(element.getType())) {
			return TGMessagesManager.getMidiInstrument(channel.getProgram()).getName();
		}
		return element.getType();
	}

	// a copy of the channel's chain
	public static TGProgram get(TGChannel channel) {
		TGSynthChannelProperties properties = properties(channel);
		TGProgram chain = TGProgramPropertiesUtil.getProgram(properties, TGSynthChannel.SOUND_CHAIN_PREFIX);
		if (chain == null) {
			chain = TGProgramPropertiesUtil.getProgram(properties, TGSynthChannel.CUSTOM_PROGRAM_PREFIX);
		}
		if (chain == null) {
			chain = new TGProgram();
			chain.setReceiver(source(channel.isAudioChannel() ? TAKES : GervillProcessorFactory.TYPE));
		}
		return chain;
	}

	// the source the type names (a General MIDI program follows the channel's Bank and Program)
	public static TGProgramElement source(String type) {
		TGProgramElement source = new TGProgramElement();
		source.setId("source");
		source.setType(type);
		return source;
	}

	// the chain into the channel's parameters, under the sound chain's keys; the custom program's are dropped
	public static void save(TGChannel channel, TGProgram chain, TGFactory factory) {
		List<TGChannelParameter> parameters = parameters(channel, chain, factory);
		channel.removeParameters();
		parameters.forEach(channel::addParameter);
	}

	private static List<TGChannelParameter> parameters(TGChannel channel, TGProgram chain, TGFactory factory) {
		List<TGChannelParameter> parameters = new ArrayList<TGChannelParameter>();
		Iterator<TGChannelParameter> kept = channel.getParameters();
		while (kept.hasNext()) {
			TGChannelParameter parameter = kept.next();
			if (!isChainKey(parameter.getKey())) {
				parameters.add(parameter.clone(factory));
			}
		}
		TGSynthChannelProperties saved = new TGSynthChannelProperties();
		TGProgramPropertiesUtil.setProgram(saved, TGSynthChannel.SOUND_CHAIN_PREFIX, chain);
		saved.getProperties().forEach((key, value) -> {
			TGChannelParameter parameter = factory.newChannelParameter();
			parameter.setKey(key);
			parameter.setValue(value);
			parameters.add(parameter);
		});
		return parameters;
	}

	private static boolean isChainKey(String key) {
		return (key.startsWith(TGSynthChannel.SOUND_CHAIN_PREFIX + ".") || key.startsWith(TGSynthChannel.CUSTOM_PROGRAM_PREFIX + "."));
	}

	private static TGSynthChannelProperties properties(TGChannel channel) {
		TGSynthChannelProperties properties = new TGSynthChannelProperties();
		Iterator<TGChannelParameter> parameters = channel.getParameters();
		while (parameters.hasNext()) {
			TGChannelParameter parameter = parameters.next();
			properties.setValue(parameter.getKey(), parameter.getValue());
		}
		return properties;
	}
}
