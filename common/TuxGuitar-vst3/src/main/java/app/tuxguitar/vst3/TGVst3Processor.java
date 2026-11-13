package app.tuxguitar.vst3;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.midi.synth.TGAudioProcessor;
import app.tuxguitar.player.base.MidiTransport;

/**
 * A VST3 plugin as a processor of the TuxGuitar Synthesizer: its element's parameters open it in the host process (the
 * plugin they name, its state, the parameters set since), and each block goes through it in 32-bit float. When the host
 * process stops, the plugin is gone: it sounds nothing, and the hosting reports the stop.
 */
abstract class TGVst3Processor implements TGAudioProcessor {

	static final int FRAMES = TGAudioBuffer.BUFFER_SIZE / 2;

	private final TGVst3Hosting hosting;
	private final boolean instrument;
	private final float[][] block = new float[TGAudioBuffer.CHANNELS][FRAMES];
	// the part of a block told one span of the transport
	private final float[][] part = new float[TGAudioBuffer.CHANNELS][FRAMES];
	// what the song does over each block, as the clock's owner writes it; stopped until one is given
	private MidiTransport transport = new MidiTransport();
	private float sampleRate = TGAudioBuffer.SAMPLE_RATE;
	private TGVst3Plugin plugin;
	private TGVst3Instance instance;

	TGVst3Processor(TGVst3Hosting hosting, boolean instrument) {
		this.hosting = hosting;
		this.instrument = instrument;
	}

	public void setSampleRate(float sampleRate) {
		this.sampleRate = sampleRate;
	}

	// the plugin the parameters name (opened again when it is another one), then its state and the parameters set since
	public void restoreParameters(Map<String, String> parameters) {
		TGVst3Plugin wanted = TGVst3Element.plugin(parameters, this.instrument);
		try {
			if (!this.plays(wanted)) {
				this.close();
				this.instance = (wanted != null ? this.hosting.open(wanted, this.sampleRate, FRAMES) : null);
				this.plugin = wanted;
			}
			this.restore(parameters);
		} catch (TGVst3Exception e) {
			this.lost();
		}
	}

	private boolean plays(TGVst3Plugin wanted) {
		return (this.instance != null && wanted != null && wanted.getModule().equals(this.plugin.getModule()) && wanted.getClassId().equals(this.plugin.getClassId()));
	}

	private void restore(Map<String, String> parameters) throws TGVst3Exception {
		if (this.instance == null) {
			return;
		}
		TGVst3State state = TGVst3Element.state(parameters);
		if (state != null) {
			this.instance.setState(state);
		}
		for (Map.Entry<Integer, Double> parameter : TGVst3Element.parameters(parameters).entrySet()) {
			this.instance.setParameter(parameter.getKey(), parameter.getValue());
		}
	}

	public void storeParameters(Map<String, String> parameters) {
		try {
			if (this.instance != null) {
				TGVst3Element.store(parameters, this.plugin, this.instance.getState());
			}
		} catch (TGVst3Exception e) {
			this.lost();
		}
	}

	public void fillBuffer(TGAudioBuffer buffer) {
		buffer.read(this.block);
		this.process(this.block);
		buffer.write(this.block);
	}

	public void setTransport(MidiTransport transport) {
		this.transport = transport;
	}

	// the block in parts, one per span of the transport, each told its span, with the notes on its frames
	public void process(float[][] samples) {
		try {
			if (this.instance != null) {
				List<TGVst3NoteEvent> events = this.takeEvents();
				MidiTransport told = this.transport;
				for (int span = 0; span < told.count(); span++) {
					int from = told.get(span).getFrame();
					int to = (span + 1 < told.count() ? told.get(span + 1).getFrame() : FRAMES);
					this.process(samples, from, to, transport(told.get(span)), TGVst3NoteEvent.between(events, from, to));
				}
				return;
			}
		} catch (TGVst3Exception e) {
			this.lost();
		}
		for (float[] side : samples) {
			java.util.Arrays.fill(side, 0f);
		}
	}

	private void process(float[][] samples, int from, int to, TGVst3Transport transport, List<TGVst3NoteEvent> events) throws TGVst3Exception {
		for (int side = 0; side < samples.length; side++) {
			System.arraycopy(samples[side], from, this.part[side], 0, to - from);
		}
		this.instance.process(this.part, to - from, transport, events);
		for (int side = 0; side < samples.length; side++) {
			System.arraycopy(this.part[side], 0, samples[side], from, to - from);
		}
	}

	private static TGVst3Transport transport(MidiTransport.Span state) {
		return new TGVst3Transport(state.isPlaying(), state.getTempo(), state.getNumerator(), state.getDenominator(), state.getQuarters(), state.getSamples());
	}

	// the note events of the block about to be processed
	List<TGVst3NoteEvent> takeEvents() {
		return Collections.emptyList();
	}

	// the host process stopped and the plugin with it (or refused it): closed, it is silent until opened again
	private void lost() {
		this.close();
	}

	public int getLatency() {
		return (this.instance != null ? this.instance.getLatency() : 0);
	}

	// the plugin as it is open in the host process, or null
	public TGVst3Instance getInstance() {
		return this.instance;
	}

	public boolean isOpen() {
		return (this.instance != null);
	}

	public boolean isBusy() {
		return false;
	}

	public void close() {
		if (this.instance != null) {
			this.instance.close();
			this.instance = null;
		}
	}

	public void finalize() {
	}
}
