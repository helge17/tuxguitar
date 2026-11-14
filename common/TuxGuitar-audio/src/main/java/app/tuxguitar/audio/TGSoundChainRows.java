package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.vst3.TGVst3Plugin;

/**
 * What an app shows of a channel's sound chain to edit it, read at one moment: a row per element (the source, then each
 * effect in processing order by its number, a disabled one in brackets) and, for the row picked (-1: none), what can be
 * done with it: an effect is removed, enabled or disabled, moved up unless it is the first; a VST3 plugin's editor
 * opens. A VST3 effect is added, a VST3 instrument or the General MIDI program made the source, except on an audio
 * channel, whose source is its takes.
 */
public final class TGSoundChainRows {

	private final TGChannel channel;
	private final TGProgram chain;

	private TGSoundChainRows(TGChannel channel, TGProgram chain) {
		this.channel = channel;
		this.chain = chain;
	}

	public static TGSoundChainRows of(TGChannel channel) {
		return new TGSoundChainRows(channel, TGSoundChain.get(channel));
	}

	// source: what the app calls the source
	public List<String> getRows(String source) {
		List<String> rows = new ArrayList<String>();
		rows.add(source + ": " + TGSoundChain.name(this.chain.getReceiver(), this.channel));
		for (int i = 0; i < this.chain.countOutputs(); i++) {
			rows.add((i + 1) + ". " + TGSoundChain.effect(this.chain.getOutput(i), this.channel));
		}
		return rows;
	}

	// the row picked: its index, or -1 for none
	public static int row(Integer picked) {
		return (picked != null ? picked : -1);
	}

	// the effect's number a row is (the plugin actions' effect), or null for the source
	public static Integer effect(int row) {
		return (row > 0 ? row : null);
	}

	public boolean isEffect(int row) {
		return row > 0;
	}

	public boolean canMoveUp(int row) {
		return row > 1;
	}

	public boolean canEdit(int row) {
		return row >= 0 && TGSoundChain.plugin(this.chain, effect(row)) != null;
	}

	// the source is always enabled
	public boolean isEnabled(int row) {
		return !this.isEffect(row) || this.chain.getOutput(row - 1).isEnabled();
	}

	// the message key of the enable/disable toggle for the row: what it would do
	public String toggleKey(int row) {
		return (this.isEnabled(row) ? "audio.sound-chain.disable" : "audio.sound-chain.enable");
	}

	public boolean canChooseSource() {
		return !this.channel.isAudioChannel();
	}

	// a plugin picked is added as an effect when it is one
	public boolean canAdd(TGVst3Plugin effect) {
		return effect != null && !effect.isInstrument();
	}

	public boolean canChoose(TGVst3Plugin instrument) {
		return instrument != null && instrument.isInstrument() && this.canChooseSource();
	}

	public static List<TGVst3Plugin> effects(List<TGVst3Plugin> plugins) {
		return plugins.stream().filter(plugin -> !plugin.isInstrument()).collect(Collectors.toList());
	}

	public static List<TGVst3Plugin> instruments(List<TGVst3Plugin> plugins) {
		return plugins.stream().filter(TGVst3Plugin::isInstrument).collect(Collectors.toList());
	}
}
