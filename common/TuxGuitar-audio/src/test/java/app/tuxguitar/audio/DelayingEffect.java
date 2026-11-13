package app.tuxguitar.audio;

import java.util.Map;

import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.midi.synth.TGAudioProcessor;
import app.tuxguitar.midi.synth.TGAudioProcessorFactory;
import app.tuxguitar.midi.synth.TGDelayLine;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.util.TGContext;

/**
 * An effect that delays what it is given by its parameter "latency", in samples, and reports that latency, as the
 * Test Latency plugin will: a sound chain with a latency, headless. Its type is "test-delay".
 */
final class DelayingEffect implements TGAudioProcessor {

	static final String TYPE = "test-delay";

	private final TGDelayLine line = new TGDelayLine();
	private int latency;
	private boolean open = true;

	// adds the effect's factory to the context's synthesizer
	static void register(TGContext context) {
		TGSynthManager.getInstance(context).addExtension(TGAudioProcessorFactory.class, new TGAudioProcessorFactory() {
			public String getType() {
				return TYPE;
			}

			public TGAudioProcessor createProcessor() {
				return new DelayingEffect();
			}
		});
	}

	public void close() {
		this.open = false;
	}

	public void finalize() {
	}

	public boolean isOpen() {
		return this.open;
	}

	public boolean isBusy() {
		return false;
	}

	public void fillBuffer(TGAudioBuffer buffer) {
		this.line.delay(buffer, this.latency);
	}

	public void storeParameters(Map<String, String> parameters) {
		parameters.put("latency", Integer.toString(this.latency));
	}

	public void restoreParameters(Map<String, String> parameters) {
		this.latency = Integer.parseInt(parameters.get("latency"));
	}

	public int getLatency() {
		return this.latency;
	}
}
