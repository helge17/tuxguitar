package app.tuxguitar.audio.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.midi.synth.TGAudioProcessor;
import app.tuxguitar.midi.synth.TGAudioProcessorFactory;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.player.base.MidiTransport;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.vst3.TGVst3EffectProcessor;
import app.tuxguitar.vst3.TGVst3Element;
import app.tuxguitar.vst3.TGVst3Exception;
import app.tuxguitar.vst3.TGVst3Hosting;
import app.tuxguitar.vst3.TGVst3Instance;
import app.tuxguitar.vst3.TGVst3PluginProcessor;

/**
 * VST3 effects as TuxGuitar runs them, each keeping, when it closes, what its plugin was told (the test plugins' report:
 * Test Tempo's records), so a scenario reads it after the render that closed it. Installed before the VST3 processors,
 * it is the VST3 effects' factory.
 */
final class ReportingEffects {

	// each closed plugin's report, by plugin name, in the order they closed
	private final Map<String, List<byte[]>> reports = new HashMap<String, List<byte[]>>();
	// what plugins do from their own editors while a render plays, each once the song reaches its musical position
	private final List<Scheduled> scheduled = new ArrayList<Scheduled>();

	interface Gesture {
		void on(TGVst3Instance plugin) throws TGVst3Exception;
	}

	private static final class Scheduled {
		private final Supplier<TGVst3Instance> plugin;
		private final double quarters;
		private final Gesture gesture;

		Scheduled(Supplier<TGVst3Instance> plugin, double quarters, Gesture gesture) {
			this.plugin = plugin;
			this.quarters = quarters;
			this.gesture = gesture;
		}

		// the plugin is the one, as it runs, and the song has reached the position
		boolean isDue(TGVst3Instance plugin, MidiTransport transport) {
			return this.reached(transport) && this.plugin.get() == plugin;
		}

		private boolean reached(MidiTransport transport) {
			return transport.count() > 0 && transport.get(0).getQuarters() >= this.quarters;
		}
	}

	// the plugin (as it runs then) does the gesture before the first block it processes from the musical position on
	void schedule(Supplier<TGVst3Instance> plugin, double quarters, Gesture gesture) {
		this.scheduled.add(new Scheduled(plugin, quarters, gesture));
	}

	// whether a gesture scheduled was not done: the render never ran its plugin from its position on
	boolean pending() {
		return !this.scheduled.isEmpty();
	}

	// the gestures due before the block the plugin is about to process, told the transport
	private void due(TGVst3Instance plugin, MidiTransport transport) throws TGVst3Exception {
		List<Scheduled> due = new ArrayList<Scheduled>();
		this.scheduled.stream().filter(next -> next.isDue(plugin, transport)).forEach(due::add);
		this.scheduled.removeAll(due);
		for (Scheduled next : due) {
			next.gesture.on(plugin);
		}
	}

	void install(TGContext context) {
		TGVst3Hosting hosting = TGVst3Hosting.getInstance(context);
		TGSynthManager.getInstance(context).addExtension(TGAudioProcessorFactory.class, new TGAudioProcessorFactory() {
			public String getType() {
				return TGVst3Element.EFFECT;
			}

			public TGAudioProcessor createProcessor() {
				return new Reporting(new TGVst3EffectProcessor(hosting));
			}
		});
	}

	List<byte[]> of(String plugin) {
		return this.reports.getOrDefault(plugin, Collections.<byte[]>emptyList());
	}

	private final class Reporting implements TGAudioProcessor, TGVst3PluginProcessor {
		private final TGVst3EffectProcessor effect;
		private String plugin;
		private MidiTransport transport = new MidiTransport();

		Reporting(TGVst3EffectProcessor effect) {
			this.effect = effect;
		}

		public TGVst3Instance getInstance() {
			return this.effect.getInstance();
		}

		public void restoreParameters(Map<String, String> parameters) {
			this.plugin = parameters.get("vst3.name");
			this.effect.restoreParameters(parameters);
		}

		public void close() {
			try {
				this.keepReport(this.effect.getInstance());
			} catch (TGVst3Exception e) {
				// a plugin whose host stopped tells nothing
			}
			this.effect.close();
		}

		private void keepReport(TGVst3Instance instance) throws TGVst3Exception {
			if (instance != null) {
				byte[] report = instance.message("TG.report", Collections.<String, Long>emptyMap(), Collections.<String, byte[]>emptyMap());
				ReportingEffects.this.reports.computeIfAbsent(this.plugin, name -> new ArrayList<byte[]>()).add(report);
			}
		}

		public void finalize() {
			this.effect.finalize();
		}

		public boolean isOpen() {
			return this.effect.isOpen();
		}

		public boolean isBusy() {
			return this.effect.isBusy();
		}

		public void fillBuffer(TGAudioBuffer buffer) {
			this.effect.fillBuffer(buffer);
		}

		public void process(float[][] block) {
			try {
				ReportingEffects.this.due(this.effect.getInstance(), this.transport);
			} catch (TGVst3Exception e) {
				throw new IllegalStateException(e);
			}
			this.effect.process(block);
		}

		public void storeParameters(Map<String, String> parameters) {
			this.effect.storeParameters(parameters);
		}

		public void setSampleRate(float sampleRate) {
			this.effect.setSampleRate(sampleRate);
		}

		public void setTransport(MidiTransport transport) {
			this.transport = transport;
			this.effect.setTransport(transport);
		}

		public int getLatency() {
			return this.effect.getLatency();
		}

		public int getTail() {
			return this.effect.getTail();
		}
	}
}
