package app.tuxguitar.audio;

import java.util.concurrent.TimeUnit;

import app.tuxguitar.midi.synth.TGAudioBufferProcessor;
import app.tuxguitar.midi.synth.TGSynthModel;
import app.tuxguitar.util.TGContext;

/**
 * The TuxGuitar Synthesizer as the player's MIDI Port has it live, headless: its General MIDI programs from Gervill
 * (as TGMidiTaps plays them), its channels opened by the player, and its output rendered a block at a time as the
 * synthesizer's thread renders it for the audio device, which plays it in real time: each block no sooner than the
 * device would ask for it, so what the player does meanwhile on threads of its own lands where it would.
 */
public final class LiveOutput {

	private static final int FRAMES = TGBlocks.FRAMES;

	private LiveOutput() {
	}

	public static TGSynthModel synthesizer(TGContext context, int sampleRate) {
		return TGMidiTaps.model(context, sampleRate);
	}

	// the output over the next frames from now (whole blocks), left and right
	public static float[][] render(TGSynthModel synthesizer, int frames) throws InterruptedException {
		TGAudioBufferProcessor blocks = new TGAudioBufferProcessor(synthesizer);
		float[][] output = new float[2][frames];
		float[][] block = new float[2][FRAMES];
		long start = System.nanoTime();
		for (int at = 0; at + FRAMES <= frames; at += FRAMES) {
			waitUntil(start + TimeUnit.SECONDS.toNanos(at) / (long) synthesizer.getSampleRate());
			blocks.process();
			blocks.getBuffer().read(block);
			TGBlocks.place(block, output, at);
		}
		return output;
	}

	private static void waitUntil(long nanos) throws InterruptedException {
		TimeUnit.NANOSECONDS.sleep(Math.max(0, nanos - System.nanoTime()));
	}
}
