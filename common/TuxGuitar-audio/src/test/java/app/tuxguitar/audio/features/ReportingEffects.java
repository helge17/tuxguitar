package app.tuxguitar.audio.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

/**
 * VST3 effects as TuxGuitar runs them, each keeping, when it closes, what its plugin was told (the test plugins' report:
 * Test Tempo's records), so a scenario reads it after the render that closed it. Installed before the VST3 processors,
 * it is the VST3 effects' factory.
 */
final class ReportingEffects {

	// each closed plugin's report, by plugin name, in the order they closed
	private final Map<String, List<byte[]>> reports = new HashMap<String, List<byte[]>>();

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

	private final class Reporting implements TGAudioProcessor {
		private final TGVst3EffectProcessor effect;
		private String plugin;

		Reporting(TGVst3EffectProcessor effect) {
			this.effect = effect;
		}

		public void restoreParameters(Map<String, String> parameters) {
			this.plugin = parameters.get("vst3.name");
			this.effect.restoreParameters(parameters);
		}

		public void close() {
			TGVst3Instance instance = this.effect.getInstance();
			try {
				if (instance != null) {
					byte[] report = instance.message("TG.report", Collections.<String, Long>emptyMap(), Collections.<String, byte[]>emptyMap());
					ReportingEffects.this.reports.computeIfAbsent(this.plugin, name -> new ArrayList<byte[]>()).add(report);
				}
			} catch (TGVst3Exception e) {
				// a plugin whose host stopped tells nothing
			}
			this.effect.close();
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
			this.effect.process(block);
		}

		public void storeParameters(Map<String, String> parameters) {
			this.effect.storeParameters(parameters);
		}

		public void setSampleRate(float sampleRate) {
			this.effect.setSampleRate(sampleRate);
		}

		public void setTransport(MidiTransport transport) {
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
