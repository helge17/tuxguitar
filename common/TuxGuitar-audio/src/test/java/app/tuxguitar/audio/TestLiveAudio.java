package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import app.tuxguitar.audio.action.TGRecordingVeto;
import app.tuxguitar.audio.device.TGAudioDeviceInfo;
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
