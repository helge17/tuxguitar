package app.tuxguitar.util.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatManager;
import app.tuxguitar.io.base.TGSongExporter;
import app.tuxguitar.io.base.TGSongStream;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.base.TGSongWriterHandle;
import app.tuxguitar.io.plugin.TGSongExporterPlugin;
import app.tuxguitar.io.plugin.TGSongWriterPlugin;
import app.tuxguitar.player.base.MidiOutputPort;
import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiSequencer;
import app.tuxguitar.player.base.MidiSequencerProvider;
import app.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import app.tuxguitar.player.plugin.TGMidiSequencerProviderPlugin;
import app.tuxguitar.util.TGContext;

/**
 * Pins the plugins that register one component (a song writer, a song exporter, a MIDI output port provider, a MIDI
 * sequencer provider): connect creates the component once and registers it; disconnect unregisters it, closes it
 * when it is a provider, and forgets it, so that the next connect creates a new one. A failure is wrapped in a
 * TGPluginException.
 *
 * A plugin's life is read back as the registration after each step ("connect:yes"), then how many components were
 * created and closed.
 */
public class TestComponentPlugins {

	private static final TGFileFormat FORMAT = new TGFileFormat("Test", "audio/x-test", new String[] {"tst"});

	private final TGContext context = new TGContext();
	private final List<String> listed = new ArrayList<String>();
	private RuntimeException failure;
	private int created;
	private int closed;

	// a kind of plugin: makes a plugin of its kind, and tells whether its component is registered in the context
	private static final class Kind {
		private final String name;
		private final Supplier<TGPlugin> plugin;
		private final BooleanSupplier registered;

		Kind(String name, Supplier<TGPlugin> plugin, BooleanSupplier registered) {
			this.name = name;
			this.plugin = plugin;
			this.registered = registered;
		}
	}

	private <T> T create(T component) {
		this.created++;
		if (this.failure != null) {
			throw this.failure;
		}
		return component;
	}

	private void close() {
		this.closed++;
	}

	private TGFileFormatManager formats() {
		return TGFileFormatManager.getInstance(this.context);
	}

	// whether the player asks the test provider for its devices
	private boolean playerLists(Runnable listing) {
		this.listed.clear();
		listing.run();
		return !this.listed.isEmpty();
	}

	private final TGSongWriter writer = new TGSongWriter() {
		public TGFileFormat getFileFormat() { return FORMAT; }
		public void write(TGSongWriterHandle handle) { }
	};

	private final TGSongExporter exporter = new TGSongExporter() {
		public String getProviderId() { return "test"; }
		public String getExportName() { return "Test"; }
		public TGSongStream openStream(TGSongStreamContext context) { return null; }
	};

	private MidiOutputPortProvider portProvider() {
		return new MidiOutputPortProvider() {
			public List<MidiOutputPort> listPorts() { listed.add("ports"); return new ArrayList<MidiOutputPort>(); }
			public void closeAll() { close(); }
		};
	}

	private MidiSequencerProvider sequencerProvider() {
		return new MidiSequencerProvider() {
			public List<MidiSequencer> listSequencers() { listed.add("sequencers"); return new ArrayList<MidiSequencer>(); }
			public void closeAll() { close(); }
		};
	}

	private final Kind writers = new Kind("song writer", () -> new TGSongWriterPlugin() {
		public String getModuleId() { return "test"; }
		protected TGSongWriter createOutputStream(TGContext context) { return create(writer); }
	}, () -> formats().findSongWriters(null).contains(writer));

	private final Kind exporters = new Kind("song exporter", () -> new TGSongExporterPlugin() {
		public String getModuleId() { return "test"; }
		protected TGSongExporter createExporter(TGContext context) { return create(exporter); }
	}, () -> formats().getExporters().contains(exporter));

	private final Kind ports = new Kind("output port provider", () -> new TGMidiOutputPortProviderPlugin() {
		public String getModuleId() { return "test"; }
		protected MidiOutputPortProvider createProvider(TGContext context) { return create(portProvider()); }
	}, () -> playerLists(() -> MidiPlayer.getInstance(context).listOutputPorts()));

	private final Kind sequencers = new Kind("sequencer provider", () -> new TGMidiSequencerProviderPlugin() {
		public String getModuleId() { return "test"; }
		protected MidiSequencerProvider createProvider(TGContext context) { return create(sequencerProvider()); }
	}, () -> playerLists(() -> MidiPlayer.getInstance(context).listSequencers()));

	// connect twice, disconnect twice, connect again
	private String life(Kind kind) throws TGPluginException {
		this.created = 0;
		this.closed = 0;
		TGPlugin plugin = kind.plugin.get();
		List<String> steps = new ArrayList<String>();
		for (boolean connect : new boolean[] {true, true, false, false, true}) {
			if (connect) {
				plugin.connect(this.context);
			} else {
				plugin.disconnect(this.context);
			}
			steps.add((connect ? "connect:" : "disconnect:") + (kind.registered.getAsBoolean() ? "yes" : "no"));
		}
		return String.join(" ", steps) + " created=" + this.created + " closed=" + this.closed;
	}

	private DynamicTest lives(Kind kind, String expected) {
		return DynamicTest.dynamicTest(kind.name, () -> assertEquals(expected, life(kind)));
	}

	private DynamicTest failsWrapped(Kind kind) {
		return DynamicTest.dynamicTest(kind.name, () -> {
			this.failure = new IllegalStateException("broken");
			TGPlugin plugin = kind.plugin.get();

			TGPluginException exception = assertThrows(TGPluginException.class, () -> plugin.connect(this.context));

			assertEquals("broken", exception.getMessage());
			assertSame(this.failure, exception.getCause());
			this.failure = null;
		});
	}

	@TestFactory
	public Stream<DynamicTest> connectRegistersOneComponentUntilDisconnected() {
		return Stream.of(
			lives(writers, "connect:yes connect:yes disconnect:no disconnect:no connect:yes created=2 closed=0"),
			lives(exporters, "connect:yes connect:yes disconnect:no disconnect:no connect:yes created=2 closed=0"),
			lives(ports, "connect:yes connect:yes disconnect:no disconnect:no connect:yes created=2 closed=1"),
			lives(sequencers, "connect:yes connect:yes disconnect:no disconnect:no connect:yes created=2 closed=1"));
	}

	@TestFactory
	public Stream<DynamicTest> aFailureWhileConnectingIsWrapped() {
		return Stream.of(failsWrapped(writers), failsWrapped(exporters), failsWrapped(ports), failsWrapped(sequencers));
	}
}
