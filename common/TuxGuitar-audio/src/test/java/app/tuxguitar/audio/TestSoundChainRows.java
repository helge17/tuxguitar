package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.vst3.TGVst3Element;
import app.tuxguitar.vst3.TGVst3Plugin;

/**
 * What an app shows of a channel's sound chain to edit it: a row per element (the source, then the effects in
 * processing order, a disabled one in brackets) and, for the row picked (-1: none), what can be done with it. The
 * guitar's chain: a source "drone" (no VST3 plugin; elements other than VST3 plugins and the General MIDI program are
 * read by their type), the VST3 effect "Test Gain", then "echo", an effect of another kind, disabled.
 */
class TestSoundChainRows {

	private static final TGVst3Plugin GAIN = new TGVst3Plugin("gain.vst3", "gain", "Test Gain", false);
	private static final TGVst3Plugin SINE = new TGVst3Plugin("sine.vst3", "sine", "Test Sine", true);

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGChannel guitar = this.fixture.songManager.getChannel(this.fixture.song, this.fixture.song.getTrack(0).getChannelId());

	TestSoundChainRows() {
		TGProgram chain = TGSoundChain.get(this.guitar);
		chain.setReceiver(TGSoundChain.source("drone"));
		chain.addOutput(TGVst3Element.create("effect-1", GAIN));
		chain.addOutput(TGSoundChain.source("echo"));
		chain.getOutput(1).setId("effect-2");
		chain.getOutput(1).setEnabled(false);
		TGSoundChain.save(this.guitar, chain, this.fixture.songManager.getFactory());
	}

	// for each row from -1: "<row> <what can be done>"
	private static String can(TGSoundChainRows rows) {
		return IntStream.rangeClosed(-1, 2).mapToObj(row -> row + (rows.isEffect(row) ? " effect " + TGSoundChainRows.effect(row) : " -") + (rows.canMoveUp(row) ? " up" : "") + (rows.canEdit(row) ? " edit" : "") + " " + rows.toggleKey(row).replace("audio.sound-chain.", "")).collect(Collectors.joining(" | "));
	}

	@Test
	void theSourceThenEachEffectInProcessingOrder() {
		assertEquals("Source: drone | 1. Test Gain | 2. [echo]", String.join(" | ", TGSoundChainRows.of(this.guitar).getRows("Source")));
	}

	// the source is not moved, removed or disabled; the first effect is not moved up; only a VST3 plugin has an editor
	@Test
	void whatCanBeDoneWithTheRowPicked() {
		assertEquals("-1 - disable | 0 - disable | 1 effect 1 edit disable | 2 effect 2 up enable", can(TGSoundChainRows.of(this.guitar)));
	}

	@Test
	void aVst3InstrumentSourceHasAnEditor() {
		TGProgram chain = TGSoundChain.get(this.guitar);
		chain.setReceiver(TGVst3Element.create("source", SINE));
		TGSoundChain.save(this.guitar, chain, this.fixture.songManager.getFactory());
		assertEquals("Source: Test Sine true", TGSoundChainRows.of(this.guitar).getRows("Source").get(0) + " " + TGSoundChainRows.of(this.guitar).canEdit(0));
	}

	// an audio channel's source is its takes: no instrument nor General MIDI program is chosen for it; a plugin picked
	// is added or chosen only as what it is
	@Test
	void whatCanBeAddedOrChosen() {
		TGChannel audio = this.fixture.songManager.getChannel(this.fixture.song, this.fixture.songManager.addAudioTrack(this.fixture.song, TGAudioInput.mono(1)).getChannelId());
		TGSoundChainRows guitar = TGSoundChainRows.of(this.guitar);
		TGSoundChainRows takes = TGSoundChainRows.of(audio);
		assertEquals("guitar: source true, add true false false, choose false true false | audio: source false, choose false",
			"guitar: source " + guitar.canChooseSource() + ", add " + guitar.canAdd(GAIN) + " " + guitar.canAdd(SINE) + " " + guitar.canAdd(null) + ", choose " + guitar.canChoose(GAIN) + " " + guitar.canChoose(SINE) + " " + guitar.canChoose(null)
			+ " | audio: source " + takes.canChooseSource() + ", choose " + takes.canChoose(SINE));
	}

	@Test
	void thePluginsOfferedAsEffectsAndAsInstrumentsAndTheRowOfNoneIsMinusOne() {
		List<TGVst3Plugin> plugins = Arrays.asList(SINE, GAIN);
		assertEquals("[Test Gain] [Test Sine] -1 2", names(TGSoundChainRows.effects(plugins)) + " " + names(TGSoundChainRows.instruments(plugins)) + " " + TGSoundChainRows.row(null) + " " + TGSoundChainRows.row(2));
	}

	private static List<String> names(List<TGVst3Plugin> plugins) {
		return plugins.stream().map(TGVst3Plugin::getName).collect(Collectors.toList());
	}
}
