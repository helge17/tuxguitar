package app.tuxguitar.audio.features;

import java.util.Arrays;
import java.util.List;

import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.player.base.MidiOutputPort;
import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiSynthesizer;
import io.cucumber.java.en.Given;

/**
 * Steps on what is set outside the song: the MIDI Port, the audio device, the plugins installed.
 */
public class SessionSteps {

	private final AudioWorld world;
	private boolean portsListed;

	public SessionSteps(AudioWorld world) {
		this.world = world;
	}

	// a MIDI Port that plays nothing: only which one is open matters to the audio features
	private static class TestPort implements MidiOutputPort {
		private final String key;
		private final String name;

		TestPort(String key, String name) {
			this.key = key;
			this.name = name;
		}

		public String getKey() { return this.key; }
		public String getName() { return this.name; }
		public void open() { }
		public void close() { }
		public void check() { }
		public MidiSynthesizer getSynthesizer() { return null; }
	}

	private static final List<MidiOutputPort> PORTS = Arrays.<MidiOutputPort>asList(
		new TestPort(TGAudioSession.SYNTHESIZER_PORT_KEY, "TuxGuitar Synthesizer"),
		new TestPort("test-midi-out", "Test MIDI Out"));

	@Given("I have set the MIDI Port to {string}")
	public void setTheMidiPort(String name) throws MidiPlayerException {
		MidiPlayer player = MidiPlayer.getInstance(world.fixture().context);
		if (!portsListed) {
			player.addOutputPortProvider(new MidiOutputPortProvider() {
				public List<MidiOutputPort> listPorts() { return PORTS; }
				public void closeAll() { }
			});
			portsListed = true;
		}
		player.openOutputPort(PORTS.stream().filter(port -> port.getName().equals(name)).findFirst().get().getKey());
	}

	// only Test Interface exists so far
	@Given("I have set the audio device to {string}")
	public void setTheAudioDevice(String name) {
		world.session().setDevice(world.device());
	}

	// the plugin host comes with the VST3 slice; no implemented scenario uses a plugin yet
	@Given("the VST3 folders hold the test plugins")
	public void theVst3FoldersHoldTheTestPlugins() {
	}
}
