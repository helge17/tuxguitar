package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.tuxguitar.audio.LiveOutput;
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
	// the scenario's MIDI Ports, once listed to the player
	private List<MidiOutputPort> ports;
	private final Map<String, TGAudioDevice> devices = new HashMap<String, TGAudioDevice>();

	public SessionSteps(AudioWorld world) {
		this.world = world;
	}

	// a MIDI Port: the TuxGuitar Synthesizer's plays live on a synthesizer of the scenario's (see LiveOutput), what the
	// player plays outside the song (a note entered); the other plays nothing. Which one is open matters to the rest
	private static class TestPort implements MidiOutputPort {
		private final String key;
		private final String name;
		private final MidiSynthesizer synthesizer;

		TestPort(String key, String name, MidiSynthesizer synthesizer) {
			this.key = key;
			this.name = name;
			this.synthesizer = synthesizer;
		}

		public String getKey() { return this.key; }
		public String getName() { return this.name; }
		public void open() { }
		public void close() { }
		public void check() { }
		public MidiSynthesizer getSynthesizer() { return this.synthesizer; }
	}

	private List<MidiOutputPort> ports() {
		return Arrays.<MidiOutputPort>asList(
			new TestPort(TGAudioSession.SYNTHESIZER_PORT_KEY, "TuxGuitar Synthesizer", LiveOutput.synthesizer(world.fixture().context, TestInterface.SAMPLE_RATE)),
			new TestPort("test-midi-out", "Test MIDI Out", null));
	}

	@Given("I have set the MIDI Port to {string}")
	public void setTheMidiPort(String name) throws MidiPlayerException {
		MidiPlayer player = MidiPlayer.getInstance(world.fixture().context);
		if (this.ports == null) {
			List<MidiOutputPort> listed = this.ports = ports();
			player.addOutputPortProvider(new MidiOutputPortProvider() {
				public List<MidiOutputPort> listPorts() { return listed; }
				public void closeAll() { }
			});
		}
		player.openOutputPort(this.ports.stream().filter(port -> port.getName().equals(name)).findFirst().get().getKey());
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
