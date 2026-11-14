package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import app.tuxguitar.audio.action.TGAddVst3EffectAction;
import app.tuxguitar.audio.action.TGRecordAction;
import app.tuxguitar.audio.action.TGRecordingVeto;
import app.tuxguitar.audio.device.TGAudioDeviceInfo;
import app.tuxguitar.audio.device.TGPortAudio;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.midi.synth.TGSynthChannel;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.midi.synth.TGSynthModel;
import app.tuxguitar.midi.synth.TGSynthOutput;
import app.tuxguitar.player.base.MidiOutputPort;
import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiSynthesizer;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.vst3.TGVst3Element;
import app.tuxguitar.vst3.TGVst3Folders;
import app.tuxguitar.vst3.TGVst3Hosting;
import app.tuxguitar.vst3.TGVst3Instance;
import app.tuxguitar.vst3.TGVst3PluginProcessor;

/**
 * The app's live audio, installed as an app does at startup, on a double of the native backend listing two devices.
 * The TuxGuitar Synthesizer's MIDI Port opens its synthesizer (at 44100 Hz, the rate the desktop's renders at) as the
 * desktop's does: through the output the synth manager gives it, else its own, which writes down that it played.
 */
class TestLiveAudio {

	private final SongActionFixture fixture = SongActionFixture.onDocument();
	private final MidiPlayer player = MidiPlayer.getInstance(this.fixture.context);
	private final TGAudioSession session = TGAudioSession.getInstance(this.fixture.context);
	private final TestBackend backend = new TestBackend(TestLiveDevice.RAMPS, 0, 300).list(0, 1,
		new TGAudioDeviceInfo(0, "Mic", "MME", 2, 0, 44100),
		new TGAudioDeviceInfo(1, "Interface", "ASIO", 8, 8, 44100));
	private final TGLiveAudio audio = TGLiveAudio.install(this.fixture.context, "refused"::equals);
	private final List<String> own = new ArrayList<String>();
	private TGSynthModel synthesizer;
	private int rendered;

	private final class Port implements MidiOutputPort {
		private TGSynthOutput output;

		public String getKey() { return TGAudioSession.SYNTHESIZER_PORT_KEY; }
		public String getName() { return "TuxGuitar Synthesizer"; }
		public void check() { }

		public void open() {
			synthesizer = LiveOutput.synthesizer(fixture.context, 44100);
			synthesizer.setListener((clock, frames) -> rendered += frames);
			this.output = TGSynthOutput.open(fixture.context, synthesizer, new TGSynthOutput() {
				public boolean play(TGSynthModel played) { return own.add("played"); }
				public void stop(TGSynthModel stopped) { own.add("stopped"); }
			});
		}

		public void close() {
			this.output.stop(synthesizer);
		}

		public MidiSynthesizer getSynthesizer() {
			return synthesizer;
		}
	}

	TestLiveAudio() throws MidiPlayerException {
		List<MidiOutputPort> ports = Arrays.<MidiOutputPort>asList(new Port());
		this.player.addOutputPortProvider(new MidiOutputPortProvider() {
			public List<MidiOutputPort> listPorts() { return ports; }
			public void closeAll() { }
		});
	}

	private void openPort() {
		this.player.openOutputPort(TGAudioSession.SYNTHESIZER_PORT_KEY);
	}

	private static TGAudioSettings settings(String hostApi, String device, int inputChannels, int recordingOffset) {
		return new TGAudioSettings(hostApi, device, device, inputChannels, recordingOffset);
	}

	// the devices chosen on the backend double
	private void open(TGAudioSettings settings) {
		this.audio.use(this.backend);
		this.audio.open(settings);
	}

	@Test
	void aNativeLibraryMissingFromItsFolderIsTheProblemAndOpensNothing() {
		File folder = new File("target/no-native-library");
		this.audio.load(folder);
		this.audio.open(settings("", "", 2, 0));
		String said = "the native audio library did not load from " + folder.getAbsolutePath() + ": ";
		assertEquals("true null", this.audio.getProblem().startsWith(said) + " " + this.session.getDevice(), this.audio.getProblem());
	}

	// the Windows launcher script's library path starts empty (";lib\")
	@Test
	void theNativeFolderIsTheOneTheAppSaysElseTheFirstLibraryFolder() {
		Properties properties = new Properties();
		properties.setProperty("java.library.path", File.pathSeparator + "lib" + File.pathSeparator + "other");
		File library = TGLiveAudio.nativeFolder(properties);
		properties.setProperty(TGLiveAudio.NATIVE_PATH, "natives");
		assertEquals(new File("lib") + " " + new File("natives") + " " + new File("."), library + " " + TGLiveAudio.nativeFolder(properties) + " " + TGLiveAudio.nativeFolder(new Properties()));
	}

	// the Linux and macOS launchers' library path puts LD_LIBRARY_PATH's folders before the app's lib folder
	@Test
	void theNativeFolderIsTheFirstLibraryFolderHoldingTheNativeLibrary(@TempDir File temp) throws IOException {
		File system = new File(temp, "system");
		File lib = new File(temp, "lib");
		system.mkdirs();
		lib.mkdirs();
		Files.createFile(new File(lib, System.mapLibraryName(TGPortAudio.LIBRARY)).toPath());
		Properties properties = new Properties();
		properties.setProperty("java.library.path", system + File.pathSeparator + lib);
		assertEquals(lib, TGLiveAudio.nativeFolder(properties));
	}

	// VST3 hosting: the host process is in the native folder (even when the native audio library is not), named for the
	// system, and the plugins are those of the system's VST3 folders
	@Test
	void loadingConfiguresTheVst3HostInTheNativeFolderAndTheSystemsVst3Folders() {
		File folder = new File("target/no-native-library");
		TGVst3Hosting hosting = TGVst3Hosting.getInstance(this.fixture.context);
		Map<String, String> env = Map.of("COMMONPROGRAMFILES", "/common");
		this.audio.load(folder, system("Windows 11", "/home/me"), env);
		String windows = hosting.getExecutable() + " " + hosting.getFolders();
		this.audio.load(folder, system("Linux", "/home/me"), env);
		String linux = hosting.getExecutable() + " " + hosting.getFolders();
		assertEquals(new File(folder, "tuxguitar-vst3-host.exe") + " " + TGVst3Folders.standard("Windows 11", env, new File("/home/me")) + " | " + new File(folder, "tuxguitar-vst3-host") + " " + TGVst3Folders.standard("Linux", env, new File("/home/me")), windows + " | " + linux);
	}

	// a line for the log: how many plugins the scan of the VST3 folders found, or why none
	@Test
	void scanningTheVst3FoldersSaysWhatItFound() throws Exception {
		TGVst3Hosting hosting = TestPluginHosting.host(this.fixture.context);
		try {
			String found = this.audio.scanPlugins();
			List<File> folders = hosting.getFolders();
			hosting.configure(new File("target/no-vst3-host.exe"), folders);
			String refused = this.audio.scanPlugins();
			assertEquals("TuxGuitar audio: 10 VST3 plugins in " + folders + " | TuxGuitar audio: no VST3 plugins (", found + " | " + refused.substring(0, refused.indexOf('(') + 1));
		} finally {
			hosting.close();
		}
	}

	private static Properties system(String os, String home) {
		Properties system = new Properties();
		system.setProperty("os.name", os);
		system.setProperty("user.home", home);
		return system;
	}

	// what a track's input channels are picked from: the channels opened on the input device; none without a device
	@Test
	void theInputChannelsAreThoseOpenedOnTheDevice() {
		int none = this.audio.countInputChannels();
		this.open(settings("ASIO", "Interface", 4, 0));
		assertEquals("0 4", none + " " + this.audio.countInputChannels());
	}

	@Test
	void theSynthesizerPlaysThroughTheDeviceTheSettingsChooseAtItsOwnRate() {
		this.open(settings("ASIO", "Interface", 4, 0));
		openPort();
		this.backend.getStream().run(2);
		assertEquals("1 x4 to 1 at 44100 1024", this.backend.getStream().getOpened() + " " + this.rendered);
		assertEquals(300, this.synthesizer.getSoundingDelay() - this.synthesizer.getLatency());
		assertTrue(this.own.isEmpty(), "it played through its own output");
	}

	@Test
	void theDeviceIsTheSessionsWithTheRecordingOffsetOfTheSettings() {
		this.open(settings("ASIO", "Interface", 4, -12));
		assertEquals("44100 -12", this.session.getDevice().getSampleRate() + " " + this.session.getRecordingOffset());
	}

	// the backend's default output is not one it lists: it does not open
	@Test
	void withoutADeviceTheSynthesizerPlaysThroughItsOwnOutputAndTheProblemIsSaid() {
		this.backend.list(0, 5, new TGAudioDeviceInfo(0, "Mic", "MME", 2, 0, 44100));
		this.open(settings("", "", 2, 0));
		openPort();
		assertEquals("[played] no output device 5", this.own + " " + this.audio.getProblem());
		assertNull(this.session.getDevice());
	}

	// no native library, so no device: Record is refused with an armed track and the synthesizer's port open, and
	// nothing changes (no take, nothing plays, no recording under way)
	@Test
	void withoutADeviceRecordingIsRefused() {
		this.audio.load(new File("target/no-native-library"));
		this.audio.open(settings("", "", 2, 0));
		openPort();
		TGTrack track = this.fixture.songManager.addAudioTrack(this.fixture.song, TGAudioInput.mono(1));
		this.session.arm(track);
		this.fixture.actionContext.setAttribute(TGRecordAction.ATTRIBUTE_START, 960L);
		this.fixture.execute(new TGRecordAction(this.fixture.context));
		Object takes = this.fixture.actionContext.getAttribute(TGRecordAction.ATTRIBUTE_TAKES);
		assertEquals(" null false false", this.fixture.outcome() + " " + takes + " " + this.player.isRunning() + " " + this.session.isRecording());
	}

	@Test
	void aBackendThatDidNotLoadIsAProblemToo() {
		this.audio.fail("the native library is missing");
		assertEquals("the native library is missing", this.audio.getProblem());
	}

	// the devices chosen again: the first stream closes and the open port's synthesizer plays through the next one
	@Test
	void choosingAgainMovesTheSynthesizerOntoTheNewDevice() {
		this.open(settings("", "", 2, 0));
		openPort();
		TestBackend.Stream first = this.backend.getStream();
		this.open(settings("ASIO", "Interface", 2, 0));
		this.backend.getStream().run(1);
		assertFalse(first.isRunning(), "the first device still runs");
		assertEquals("1 x2 to 1 at 44100 512 []", this.backend.getStream().getOpened() + " " + this.rendered + " " + this.own);
		assertTrue(this.problemIsNone());
	}

	private boolean problemIsNone() {
		return this.audio.getProblem() == null;
	}

	// a song channel the synthesizer had open before it moved is open on it again
	@Test
	void theSongsChannelsAreOpenAgainOnTheMovedSynthesizer() throws MidiPlayerException {
		this.open(settings("", "", 2, 0));
		openPort();
		this.player.updateChannels();
		this.open(settings("ASIO", "Interface", 2, 0));
		int channel = this.fixture.song.getChannel(0).getChannelId();
		assertTrue(this.player.getChannelRouter().getMidiChannel(channel) instanceof TGSynthChannel);
	}

	// with or without a device: the VST3 plugins of the synthesizer's chains are the live plugins (whose editors open)
	@Test
	void theOpenedSynthesizersPluginsAreTheLivePlugins() throws Exception {
		TGVst3Hosting hosting = TestPluginHosting.host(this.fixture.context);
		TGLivePlugins live = TGLivePlugins.getInstance(this.fixture.context);
		try {
			TGChannel guitar = this.fixture.song.getChannel(0);
			this.fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, guitar);
			this.fixture.actionContext.setAttribute(TGAddVst3EffectAction.ATTRIBUTE_PLUGIN, hosting.find("Test Gain"));
			this.fixture.execute(new TGAddVst3EffectAction(this.fixture.context));
			openPort();
			TGSynthChannel played = TestPluginHosting.play(this.synthesizer, this.fixture.songManager, this.fixture.song, guitar);
			TGVst3Instance running = ((TGVst3PluginProcessor) played.getProcessor().getAudioProcessor(played.getProgram().getOutput(0))).getInstance();
			assertTrue(running != null && running == live.find(guitar.getChannelId(), "effect-1"));
		} finally {
			live.close();
			hosting.close();
		}
	}

	// what an app installs gives the synthesizer VST3 effects and instruments to run
	@Test
	void installedTheSynthesizerRunsVst3EffectsAndInstruments() {
		TGSynthManager manager = TGSynthManager.getInstance(this.fixture.context);
		assertEquals("true true", (manager.findAudioProcessorFactory(TGVst3Element.EFFECT) != null) + " " + (manager.findMidiProcessorFactory(TGVst3Element.INSTRUMENT) != null));
	}

	@Test
	void closedTheDeviceStopsAndTheSessionHasNone() {
		this.open(settings("", "", 2, 0));
		this.audio.close();
		assertFalse(this.backend.getStream().isRunning());
		assertNull(this.session.getDevice());
	}

	// what an app installs: audio tracks sound their takes, the actions given are refused while recording too; installed
	// again (a plugin connected again), nothing is installed twice
	@Test
	void installedTakesSoundAndTheActionsGivenAreRefusedWhileRecordingOnce() {
		TGLiveAudio.install(this.fixture.context, id -> false);
		this.session.setRecording(true);
		boolean refused = TGRecordingVeto.getInstance(this.fixture.context).intercept("refused", null);
		int outputs = TGSynthManager.getInstance(this.fixture.context).getExtensions(TGSynthOutput.class).size();
		assertEquals("true true 1", (TGSynthManager.getInstance(this.fixture.context).findMidiProcessorFactory(TGSynthChannel.TAKES) != null) + " " + refused + " " + outputs);
	}

	// the port closed, its synthesizer plays through the device no more
	@Test
	void aClosedPortsSynthesizerIsNotPlayed() {
		this.open(settings("", "", 2, 0));
		openPort();
		this.backend.getStream().run(1);
		this.player.closeOutputPort();
		this.backend.getStream().run(1);
		assertEquals(512, this.rendered);
	}

	// what the log and the settings say
	@Test
	void itDescribesTheDeviceOrWhyThereIsNone() {
		this.open(settings("ASIO", "Interface", 2, 0));
		String open = this.audio.describe();
		this.audio.fail("no library");
		assertEquals("TuxGuitar audio: Interface (ASIO) at 44100 Hz, latency in 0 out 300 frames | TuxGuitar audio: no device (no library), the TuxGuitar Synthesizer plays through Java Sound", open + " | " + this.audio.describe());
	}

	// what the settings offer: the backend's host APIs and the devices of one; nothing without a backend
	@Test
	void theHostApisAndDevicesToChooseFrom() {
		String none = this.audio.getHostApis() + " " + this.audio.getInputs("MME") + " " + this.audio.getOutputs("ASIO");
		this.audio.use(this.backend);
		assertEquals("[] [] [] | [MME, ASIO] [Mic] [Interface]", none + " | " + this.audio.getHostApis() + " " + this.audio.getInputs("MME") + " " + this.audio.getOutputs("ASIO"));
	}
}
