package app.tuxguitar.audio.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

import org.junit.jupiter.api.Test;

import app.tuxguitar.audio.TGSoundChain;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.midi.synth.TGSynthChannel;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGChannelParameter;
import app.tuxguitar.vst3.TGVst3Element;
import app.tuxguitar.vst3.TGVst3Plugin;

/**
 * The sound chain actions on a channel, without a plugin host: what they refuse, and how the chain is kept in the
 * channel's parameters. A result reads as the chain, then "success" when the action reported it.
 */
public class TestSoundChainActions {

	private static final TGVst3Plugin GAIN = new TGVst3Plugin("gain.vst3", "1", "Gain", false);
	private static final TGVst3Plugin SYNTH = new TGVst3Plugin("synth.vst3", "2", "Synth", true);

	private final SongActionFixture fixture = SongActionFixture.onDocument();
	private final TGChannel guitar = fixture.songManager.getChannel(fixture.song, fixture.song.getTrack(0).getChannelId());

	private String run(TGChannel channel, TGSoundChainAction action) {
		fixture.actionContext.setAttribute(TGActionBase.ATTRIBUTE_SUCCESS, null);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
		fixture.execute(action);
		return read(channel) + (Boolean.TRUE.equals(fixture.actionContext.getAttribute(TGActionBase.ATTRIBUTE_SUCCESS)) ? " success" : "");
	}

	// the chain's elements by type, a VST3 plugin by name (the General MIDI names need the app's messages)
	private static String read(TGChannel channel) {
		TGProgram chain = TGSoundChain.get(channel);
		StringJoiner reading = new StringJoiner(" > ");
		reading.add(name(chain.getReceiver()));
		for (int i = 0; i < chain.countOutputs(); i++) {
			reading.add(name(chain.getOutput(i)));
		}
		return reading.toString();
	}

	private static String name(TGProgramElement element) {
		return (TGVst3Element.isVst3(element) ? TGVst3Element.getName(element) : element.getType());
	}

	private String add(TGVst3Plugin plugin) {
		fixture.actionContext.setAttribute(TGAddVst3EffectAction.ATTRIBUTE_PLUGIN, plugin);
		return run(guitar, new TGAddVst3EffectAction(fixture.context));
	}

	private String onEffect(int effect, TGEffectAction action) {
		fixture.actionContext.setAttribute(TGEffectAction.ATTRIBUTE_EFFECT, effect);
		return run(guitar, action);
	}

	private String ids() {
		TGProgram chain = TGSoundChain.get(guitar);
		List<String> ids = new ArrayList<String>();
		for (int i = 0; i < chain.countOutputs(); i++) {
			ids.add(chain.getOutput(i).getId());
		}
		return String.join(" ", ids);
	}

	@Test
	public void eachEffectAddedGetsAnIdNoOtherEffectHas() {
		add(GAIN);
		add(GAIN);
		onEffect(1, new TGRemoveEffectAction(fixture.context));
		assertEquals("gervill > Gain > Gain success", add(GAIN));
		assertEquals("effect-2 effect-3", ids());
	}

	@Test
	public void changesToAnEffectNotThereAreRefused() {
		add(GAIN);
		assertEquals("gervill > Gain", onEffect(1, new TGMoveEffectUpAction(fixture.context)));
		assertEquals("gervill > Gain", onEffect(2, new TGRemoveEffectAction(fixture.context)));
		assertEquals("gervill > Gain", onEffect(0, new TGRemoveEffectAction(fixture.context)));
	}

	@Test
	public void aParameterIsSetOnlyOnAVst3Plugin() {
		add(GAIN);
		fixture.actionContext.setAttribute(TGSetPluginParameterAction.ATTRIBUTE_PARAMETER, 1);
		fixture.actionContext.setAttribute(TGSetPluginParameterAction.ATTRIBUTE_VALUE, 0.5);
		fixture.actionContext.setAttribute(TGEffectAction.ATTRIBUTE_EFFECT, null);
		assertEquals("gervill > Gain", run(guitar, new TGSetPluginParameterAction(fixture.context)));
		fixture.actionContext.setAttribute(TGEffectAction.ATTRIBUTE_EFFECT, 2);
		assertEquals("gervill > Gain", run(guitar, new TGSetPluginParameterAction(fixture.context)));
		fixture.actionContext.setAttribute(TGEffectAction.ATTRIBUTE_EFFECT, 1);
		assertEquals("gervill > Gain success", run(guitar, new TGSetPluginParameterAction(fixture.context)));
		assertEquals("0.5", TGSoundChain.get(guitar).getOutput(0).getParameters().get("vst3.parameter.1"));
	}

	@Test
	public void anAudioChannelKeepsItsTakesAsTheSource() {
		TGChannel audio = fixture.songManager.getChannel(fixture.song, fixture.songManager.addAudioTrack(fixture.song, TGAudioInput.mono(1)).getChannelId());
		fixture.actionContext.setAttribute(TGAddVst3EffectAction.ATTRIBUTE_PLUGIN, SYNTH);
		assertEquals("takes", run(audio, new TGChooseVst3InstrumentAction(fixture.context)));
		assertEquals("takes", run(audio, new TGChooseGeneralMidiAction(fixture.context)));
		assertEquals("Synth success", run(guitar, new TGChooseVst3InstrumentAction(fixture.context)));
		assertEquals("gervill success", run(guitar, new TGChooseGeneralMidiAction(fixture.context)));
		assertEquals("gervill", run(guitar, new TGChooseTakesAction(fixture.context)));
		assertEquals("takes success", run(audio, new TGChooseTakesAction(fixture.context)));
	}

	private void parameter(String key, String value) {
		TGChannelParameter parameter = fixture.songManager.getFactory().newChannelParameter();
		parameter.setKey(key);
		parameter.setValue(value);
		guitar.addParameter(parameter);
	}

	private List<String> keys() {
		List<String> keys = new ArrayList<String>();
		Iterator<TGChannelParameter> parameters = guitar.getParameters();
		parameters.forEachRemaining(parameter -> keys.add(parameter.getKey()));
		keys.sort(null);
		return keys;
	}

	// a song of an older version keeps its sound chain as a custom program; changed, it is kept apart from it
	@Test
	public void aCustomProgramOfAnOlderVersionIsTheChainAndIsKeptApartOnceChanged() {
		parameter(TGSynthChannel.CUSTOM_PROGRAM_PREFIX + ".id", "custom");
		parameter(TGSynthChannel.CUSTOM_PROGRAM_PREFIX + ".type", "lv2");
		parameter("other.key", "kept");
		assertEquals("lv2", read(guitar));
		assertEquals("lv2 > Gain success", add(GAIN));
		assertEquals("[other.key, sound.chain.enabled, sound.chain.id, sound.chain.output.0.enabled, sound.chain.output.0.id, "
			+ "sound.chain.output.0.param.0.name, sound.chain.output.0.param.0.value, sound.chain.output.0.param.1.name, "
			+ "sound.chain.output.0.param.1.value, sound.chain.output.0.param.2.name, sound.chain.output.0.param.2.value, "
			+ "sound.chain.output.0.param.count, sound.chain.output.0.type, sound.chain.output.count, sound.chain.param.count, "
			+ "sound.chain.type]", keys().toString());
	}
}
