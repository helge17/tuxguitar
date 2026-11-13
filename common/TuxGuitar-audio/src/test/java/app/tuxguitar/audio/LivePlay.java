package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import app.tuxguitar.midi.synth.TGAudioBufferProcessor;
import app.tuxguitar.midi.synth.TGSynthModel;
import app.tuxguitar.player.base.MidiPlayer;

/**
 * The song played live, headless, as the app plays it on the TuxGuitar Synthesizer: the player started, its sequencer
 * on the synthesizer's sample clock, the synthesizer's blocks rendered as its thread renders them, the play loop taking
 * a turn after each, until the play loop ends. What is heard is kept: the output, left and right.
 */
public final class LivePlay {

	private static final int FRAMES = TGBlocks.FRAMES;
	private static final int MOST_BLOCKS = 10000;

	private LivePlay() {
	}

	// the player has started to play on the synthesizer
	public static float[][] play(MidiPlayer player, TGSynthModel synthesizer) {
		TGAudioBufferProcessor blocks = new TGAudioBufferProcessor(synthesizer);
		List<float[][]> heard = new ArrayList<float[][]>();
		// a block, then a turn of the play loop, until a turn ends it
		IntStream.range(0, MOST_BLOCKS).peek(block -> heard.add(block(blocks))).allMatch(block -> player.runPlayLoopProcess());
		float[][] output = new float[2][heard.size() * FRAMES];
		IntStream.range(0, heard.size()).forEach(block -> TGBlocks.place(heard.get(block), output, (long) block * FRAMES));
		return output;
	}

	private static float[][] block(TGAudioBufferProcessor blocks) {
		float[][] block = new float[2][FRAMES];
		blocks.process();
		blocks.getBuffer().read(block);
		return block;
	}
}
