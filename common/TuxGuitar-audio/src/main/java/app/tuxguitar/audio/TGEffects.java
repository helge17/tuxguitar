package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.midi.synth.TGAudioProcessor;
import app.tuxguitar.midi.synth.TGAudioProcessorFactory;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.player.base.MidiTransport;
import app.tuxguitar.util.TGContext;

/**
 * The enabled effects of a sound chain whose source the audio engine renders (an audio channel's takes), open at the
 * audio device's rate: a tap goes through them in blocks of 512 samples, 32-bit float, so the chain has room above full
 * scale. Offline their latency is taken out (see TGBlocks): the blocks run on past the end of the tap by the latency,
 * and each lands in the tap that much earlier. A disabled effect, or one no processor factory makes, is passed by.
 */
final class TGEffects {

	private static final int FRAMES = TGBlocks.FRAMES;

	private final List<TGAudioProcessor> effects;
	// what the song does over the block the effects process
	private final MidiTransport transport;

	private TGEffects(List<TGAudioProcessor> effects, MidiTransport transport) {
		this.effects = effects;
		this.transport = transport;
	}

	static TGEffects open(TGContext context, TGProgram chain, int sampleRate) {
		MidiTransport transport = new MidiTransport();
		TGSynthManager manager = TGSynthManager.getInstance(context);
		List<TGAudioProcessor> effects = new ArrayList<TGAudioProcessor>();
		for (int i = 0; i < chain.countOutputs(); i++) {
			TGProgramElement element = chain.getOutput(i);
			TGAudioProcessorFactory factory = manager.findAudioProcessorFactory(element.getType());
			if (element.isEnabled() && factory != null) {
				TGAudioProcessor effect = factory.createProcessor();
				effect.setSampleRate(sampleRate);
				effect.setTransport(transport);
				effect.restoreParameters(element.getParameters());
				effects.add(effect);
			}
		}
		return new TGEffects(effects, transport);
	}

	// how many samples later than its input the chain sounds it
	int getLatency() {
		int latency = 0;
		for (TGAudioProcessor effect : this.effects) {
			latency += effect.getLatency();
		}
		return latency;
	}

	// how many samples the chain sounds on after its input stops: the tails along it
	int getTail() {
		int tail = 0;
		for (TGAudioProcessor effect : this.effects) {
			tail = TGAudioProcessor.along(tail, effect.getTail());
		}
		return tail;
	}

	// the tap through the effects, its latency taken out, as long as the tap; each block is told what the song does
	// over it on the timeline
	TGTap process(float[][] tap, TGTimeline timeline) {
		int latency = this.getLatency();
		int length = tap[0].length;
		float[][] processed = new float[tap.length][length];
		float[][] block = new float[tap.length][FRAMES];
		for (long start = 0; start < length + latency; start += FRAMES) {
			for (int side = 0; side < tap.length; side++) {
				java.util.Arrays.fill(block[side], 0f);
				TGBlocks.take(tap[side], start, block[side]);
			}
			timeline.tell(this.transport, start, FRAMES);
			for (TGAudioProcessor effect : this.effects) {
				effect.process(block);
			}
			TGBlocks.place(block, processed, start - latency);
		}
		return new TGTap(processed, latency, this.getTail());
	}

	void close() {
		this.effects.forEach(TGAudioProcessor::close);
	}
}
