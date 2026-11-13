package app.tuxguitar.midi.synth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

import app.tuxguitar.player.base.MidiTransport;

/**
 * Processors for the synthesizer's tests: a source that sounds a constant level on both sides and keeps what it was
 * sent, and effects that change each sample by a function. Their factories are registered by type.
 */
final class FakeProcessors {

	static final int FRAMES = TGAudioBuffer.BUFFER_SIZE / 2;

	private FakeProcessors() {
	}

	static class Effect implements TGAudioProcessor {
		private final DoubleUnaryOperator change;
		final Map<String, String> restored = new HashMap<String, String>();
		boolean open = true;
		boolean busy;
		// what it reports, not what it does
		int latency;
		// the transport it was given
		MidiTransport transport;

		Effect(DoubleUnaryOperator change) {
			this.change = change;
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
			return this.busy;
		}

		public void fillBuffer(TGAudioBuffer buffer) {
			float[][] samples = read(buffer);
			this.change(samples);
			buffer.write(samples);
		}

		void change(float[][] samples) {
			for (float[] side : samples) {
				for (int i = 0; i < side.length; i++) {
					side[i] = (float) this.change.applyAsDouble(side[i]);
				}
			}
		}

		public void storeParameters(Map<String, String> parameters) {
			parameters.putAll(this.restored);
		}

		public void restoreParameters(Map<String, String> parameters) {
			this.restored.putAll(parameters);
		}

		public int getLatency() {
			return this.latency;
		}

		public void setTransport(MidiTransport transport) {
			this.transport = transport;
		}
	}

	// an effect that works on 32-bit float blocks, as a plugin does: what goes above full scale stays in the block
	static class FloatEffect extends Effect {

		FloatEffect(DoubleUnaryOperator change) {
			super(change);
		}

		public void process(float[][] block) {
			this.change(block);
		}
	}

	// a source that cannot time events: a timed one reaches it at once, as TGMidiProcessor does by default
	static class Source extends Effect implements TGMidiProcessor {
		final List<String> received = new ArrayList<String>();
		// written after the event being sent
		String timing = "";

		Source(float level) {
			super(sample -> level);
		}

		private void record(String event) {
			this.received.add(event + this.timing);
		}

		public void sendNoteOn(int key, int velocity, int voice, boolean bendMode) {
			this.record("on " + key + " " + velocity + " " + voice + " " + bendMode);
		}

		public void sendNoteOff(int key, int velocity, int voice, boolean bendMode) {
			this.record("off " + key + " " + velocity + " " + voice + " " + bendMode);
		}

		public void sendPitchBend(int value, int voice, boolean bendMode) {
			this.record("bend " + value + " " + voice + " " + bendMode);
		}

		public void sendControlChange(int controller, int value) {
			this.record("cc " + controller + " " + value);
		}

		public void setProgram(int bank, int program) {
			this.record("program " + bank + ":" + program);
		}
	}

	// a source that times events: it writes " @frame" after a timed one
	static class TimedSource extends Source {

		TimedSource(float level) {
			super(level);
		}

		private void at(long frame, Runnable send) {
			this.timing = " @" + frame;
			send.run();
			this.timing = "";
		}

		public void sendNoteOn(int key, int velocity, int voice, boolean bendMode, long frame) {
			this.at(frame, () -> this.sendNoteOn(key, velocity, voice, bendMode));
		}

		public void sendNoteOff(int key, int velocity, int voice, boolean bendMode, long frame) {
			this.at(frame, () -> this.sendNoteOff(key, velocity, voice, bendMode));
		}

		public void sendPitchBend(int value, int voice, boolean bendMode, long frame) {
			this.at(frame, () -> this.sendPitchBend(value, voice, bendMode));
		}

		public void sendControlChange(int controller, int value, long frame) {
			this.at(frame, () -> this.sendControlChange(controller, value));
		}
	}

	static class SourceFactory implements TGMidiProcessorFactory {
		private final String type;
		private final float level;
		private final boolean timed;
		final List<Source> created = new ArrayList<Source>();

		SourceFactory(String type, float level) {
			this(type, level, true);
		}

		SourceFactory(String type, float level, boolean timed) {
			this.type = type;
			this.level = level;
			this.timed = timed;
		}

		public String getType() {
			return this.type;
		}

		public Source createProcessor() {
			Source source = (this.timed ? new TimedSource(this.level) : new Source(this.level));
			this.created.add(source);
			return source;
		}

		Source last() {
			return this.created.get(this.created.size() - 1);
		}
	}

	static class EffectFactory implements TGAudioProcessorFactory {
		private final String type;
		private final DoubleUnaryOperator change;
		private final boolean floating;
		final List<Effect> created = new ArrayList<Effect>();

		EffectFactory(String type, DoubleUnaryOperator change) {
			this(type, change, false);
		}

		// floating: its effects work on float blocks (FloatEffect)
		EffectFactory(String type, DoubleUnaryOperator change, boolean floating) {
			this.type = type;
			this.change = change;
			this.floating = floating;
		}

		public String getType() {
			return this.type;
		}

		public Effect createProcessor() {
			Effect effect = (this.floating ? new FloatEffect(this.change) : new Effect(this.change));
			this.created.add(effect);
			return effect;
		}
	}

	static float[][] read(TGAudioBuffer buffer) {
		float[][] samples = new float[TGAudioBuffer.CHANNELS][FRAMES];
		buffer.read(samples);
		return samples;
	}

	static TGAudioBuffer level(float left, float right) {
		float[][] samples = new float[TGAudioBuffer.CHANNELS][FRAMES];
		java.util.Arrays.fill(samples[0], left);
		java.util.Arrays.fill(samples[1], right);
		TGAudioBuffer buffer = new TGAudioBuffer();
		buffer.write(samples);
		return buffer;
	}

	static TGProgramElement element(String id, String type) {
		TGProgramElement element = new TGProgramElement();
		element.setId(id);
		element.setType(type);
		return element;
	}

	// a program of the receiver type and the effects types, in processing order; ids are the types
	static TGProgram program(String receiver, String... outputs) {
		TGProgram program = new TGProgram();
		program.setReceiver(element(receiver, receiver));
		for (String output : outputs) {
			program.addOutput(element(output, output));
		}
		return program;
	}
}
