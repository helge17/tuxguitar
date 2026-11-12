package app.tuxguitar.midi.synth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;
import app.tuxguitar.util.properties.TGProperties;
import app.tuxguitar.util.properties.TGPropertiesManager;

/**
 * Pins how the synthesizer finds its processor factories (plugins add them as extensions) and how it loads the
 * programs kept in its settings.
 */
public class TestSynthManager {

	private final TGContext context = new TGContext();
	private final TGSynthManager manager = TGSynthManager.getInstance(this.context);
	private final FakeProcessors.SourceFactory source = new FakeProcessors.SourceFactory("source", 0.5f);
	private final FakeProcessors.EffectFactory effect = new FakeProcessors.EffectFactory("effect", sample -> sample);

	private class Plugin extends TGSynthExtensionPlugin {
		private final boolean fails;

		Plugin(boolean fails) {
			this.fails = fails;
		}

		public String getModuleId() {
			return "test";
		}

		public List<TGSynthExtension<?>> createExtensions(TGContext context) {
			if (this.fails) {
				throw new IllegalStateException("no extensions");
			}
			return Arrays.<TGSynthExtension<?>>asList(
				new TGSynthExtension<TGMidiProcessorFactory>(TGMidiProcessorFactory.class, TestSynthManager.this.source),
				new TGSynthExtension<TGAudioProcessorFactory>(TGAudioProcessorFactory.class, TestSynthManager.this.effect));
		}
	}

	@Test
	public void factoriesAreFoundByType() {
		this.manager.addExtension(TGMidiProcessorFactory.class, this.source);
		this.manager.addExtension(TGAudioProcessorFactory.class, this.effect);
		assertSame(this.source, this.manager.findMidiProcessorFactory("source"));
		assertSame(this.effect, this.manager.findAudioProcessorFactory("effect"));
		assertNull(this.manager.findMidiProcessorFactory("effect"));
		assertNull(this.manager.findAudioProcessorFactory("source"));
		assertEquals(Arrays.asList("source"), this.manager.getAllSupportedMidiTypes());
		assertEquals(Arrays.asList("effect"), this.manager.getAllSupportedAudioTypes());
		assertSame(this.manager, TGSynthManager.getInstance(this.context));
	}

	@Test
	public void removedFactoriesAreNotFound() {
		this.manager.addExtension(TGMidiProcessorFactory.class, this.source);
		this.manager.removeExtension(TGMidiProcessorFactory.class, this.source);
		this.manager.removeExtension(TGMidiProcessorFactory.class, this.source);
		assertNull(this.manager.findMidiProcessorFactory("source"));
		assertTrue(this.manager.getExtensions(TGMidiProcessorFactory.class).isEmpty());
	}

	@Test
	public void anExtensionPluginAddsItsFactoriesOnceAndRemovesThem() throws TGPluginException {
		Plugin plugin = new Plugin(false);
		plugin.connect(this.context);
		plugin.connect(this.context);
		assertEquals(1, this.manager.getExtensions(TGMidiProcessorFactory.class).size());
		assertSame(this.effect, this.manager.findAudioProcessorFactory("effect"));
		plugin.disconnect(this.context);
		plugin.disconnect(this.context);
		assertTrue(this.manager.getExtensions(TGMidiProcessorFactory.class).isEmpty());
		assertTrue(this.manager.getExtensions(TGAudioProcessorFactory.class).isEmpty());
		plugin.connect(this.context);
		assertEquals(1, this.manager.getExtensions(TGAudioProcessorFactory.class).size());
	}

	@Test
	public void anExtensionPluginThatFailsReportsIt() {
		TGPluginException thrown = assertThrows(TGPluginException.class, () -> new Plugin(true).connect(this.context));
		assertEquals("no extensions", thrown.getMessage());
	}

	@Test
	public void anExtensionKeepsItsClassAndObject() {
		TGSynthExtension<TGMidiProcessorFactory> extension = new TGSynthExtension<TGMidiProcessorFactory>(TGMidiProcessorFactory.class, this.source);
		assertSame(TGMidiProcessorFactory.class, extension.getExtensionClass());
		assertSame(this.source, extension.getExtension());
	}

	// the settings read the module's configuration: "synth.program.<bank>.<program>" keys
	private TGSynthSettings settings(String... keysAndValues) {
		TGPropertiesManager properties = TGPropertiesManager.getInstance(this.context);
		properties.setPropertiesFactory(TGSynthChannelProperties::new);
		properties.addPropertiesReader("config", (TGProperties read, String module) -> {
			if (module.equals("tuxguitar-synth")) {
				for (int i = 0; i < keysAndValues.length; i += 2) {
					read.setValue(keysAndValues[i], keysAndValues[i + 1]);
				}
			}
		});
		return new TGSynthSettings(this.context);
	}

	@Test
	public void theSettingsLoadTheProgramsTheyKeep() {
		TGSynthModel model = new TGSynthModel(this.context);
		settings("synth.program.128.3.id", "drums", "synth.program.128.3.type", "source", "synth.audio.buffer.size", "4").loadPrograms(model);
		assertEquals("drums", model.getProgram(128, 3).getReceiver().getId());
		assertNull(model.getProgram(0, 3).getReceiver());
	}

	@Test
	public void theSettingsHaveDefaults() {
		TGSynthSettings settings = settings();
		assertEquals(10, settings.getAudioBufferSize().intValue());
		assertEquals(60982, settings.getRemoteHostServerPort().intValue());
		assertSame(settings.getConfig(), settings.getConfig());
		settings.save();
	}

	@Test
	public void theSettingsReadTheirValues() {
		TGSynthSettings settings = settings("synth.audio.buffer.size", "4", "synth.host.default.server.port", "1234");
		assertEquals(4, settings.getAudioBufferSize().intValue());
		assertEquals(1234, settings.getRemoteHostServerPort().intValue());
	}
}
