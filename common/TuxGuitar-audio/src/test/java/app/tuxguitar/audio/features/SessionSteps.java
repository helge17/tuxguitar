package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.tuxguitar.audio.TGAudioDevice;
import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.player.base.MidiOutputPort;
import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiSynthesizer;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

/**
 * Steps on what is set outside the song: the MIDI Port, the audio device.
 */
public class SessionSteps {

	private final AudioWorld world;
	private boolean portsListed;
	private final Map<String, TGAudioDevice> devices = new HashMap<String, TGAudioDevice>();

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

	@Given("I have set the audio device to {string}")
	public void setTheAudioDevice(String name) {
		world.session().setDevice(device(name));
	}

	// a TuxGuitar setting: opening a song leaves it as it is
	@Then("the audio device is {string}")
	public void theAudioDeviceIs(String name) {
		assertSame(device(name), world.session().getDevice());
	}

	// Test Interface is the world's; any other name is a Test Interface of its own, made once
	private TGAudioDevice device(String name) {
		return ("Test Interface".equals(name) ? world.device() : this.devices.computeIfAbsent(name, key -> new TestInterface()));
	}

	@Given("I have set the audio device to {string} with {int} samples of input latency and {int} of output latency")
	public void setTheAudioDeviceWithLatency(String name, int input, int output) {
		world.device().setLatency(input, output);
		setTheAudioDevice(name);
	}

	// the device reports another input latency than it has
	@Given("I have set the audio device to {string} with {int} samples of input latency, reporting {int}")
	public void setTheAudioDeviceReporting(String name, int input, int reported) {
		world.device().setLatency(input, 0);
		world.device().reportInputLatency(reported);
		setTheAudioDevice(name);
	}

	// a setting of the session, as the audio settings set it
	@Given("I have set the recording offset to {int} samples")
	public void setTheRecordingOffset(int samples) {
		world.session().setRecordingOffset(samples);
	}
}
