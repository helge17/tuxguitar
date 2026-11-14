package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.audio.device.TGAudioBackend;
import app.tuxguitar.audio.device.TGAudioDeviceInfo;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.configuration.TGConfigManager;
import app.tuxguitar.util.properties.TGPropertiesManager;

/**
 * TuxGuitar's choice of audio devices: kept in its configuration, and chosen on the backend's devices. The backend lists
 * a microphone and speakers on MME (the defaults), the speakers on WASAPI too, an 8-channel interface on ASIO and a mono headset on MME.
 */
class TestAudioSettings {

	private final TGContext context = new TGContext();
	private final TGAudioBackend backend = new TestBackend(TestLiveDevice.RAMPS, 0, 0).list(0, 1,
		new TGAudioDeviceInfo(0, "Mic", "MME", 2, 0, 44100),
		new TGAudioDeviceInfo(1, "Speakers", "MME", 0, 2, 44100),
		new TGAudioDeviceInfo(2, "Speakers", "WASAPI", 0, 2, 48000),
		new TGAudioDeviceInfo(3, "Interface", "ASIO", 8, 8, 48000),
		new TGAudioDeviceInfo(4, "Headset", "MME", 1, 1, 44100));

	TestAudioSettings() {
		TGPropertiesManager.getInstance(this.context).setPropertiesFactory(MapProperties::new);
	}

	private TGConfigManager config() {
		return new TGConfigManager(this.context, "tuxguitar-audio");
	}

	private static String describe(TGAudioSettings settings) {
		return settings.getHostApi() + "|" + settings.getInput() + "|" + settings.getOutput() + "|" + settings.getInputChannels() + "|" + settings.getRecordingOffset();
	}

	// "<input device> x<input channels> to <output device>"
	private String chosen(TGAudioSettings settings) {
		int input = settings.chooseInput(this.backend);
		return input + " x" + settings.countInputChannels(this.backend, input) + " to " + settings.chooseOutput(this.backend);
	}

	@Test
	void nothingChosenYetIsTheDefaultDevicesWithTwoInputChannels() {
		assertEquals("|||2|0", describe(TGAudioSettings.read(config())));
	}

	@Test
	void theChoiceIsReadBackAsWritten() {
		TGConfigManager config = config();
		new TGAudioSettings("ASIO", "Interface", "Interface", 4, -12).write(config);
		assertEquals("ASIO|Interface|Interface|4|-12", describe(TGAudioSettings.read(config)));
	}

	// none named; or the speakers named as input and the microphone as output, which cannot serve so
	@Test
	void theDefaultDevicesWhenNoneThatServesIsNamed() {
		assertEquals("0 x2 to 1 | 0 x2 to 1", chosen(new TGAudioSettings("", "", "", 2, 0)) + " | " + chosen(new TGAudioSettings("MME", "Speakers", "Mic", 2, 0)));
	}

	// a mono headset serves with its one channel each way
	@Test
	void aDeviceIsTheOneOfThatNameOnTheHostApiWithAtMostTheChannelsItHas() {
		assertEquals("0 x2 to 2 | 3 x4 to 3 | 3 x8 to 3 | 4 x1 to 4", chosen(new TGAudioSettings("WASAPI", "", "Speakers", 2, 0))
			+ " | " + chosen(new TGAudioSettings("ASIO", "Interface", "Interface", 4, 0)) + " | " + chosen(new TGAudioSettings("ASIO", "Interface", "Interface", 16, 0))
			+ " | " + chosen(new TGAudioSettings("MME", "Headset", "Headset", 2, 0)));
	}

	// to choose from: the devices of a host API that have inputs, or outputs, in the backend's order
	@Test
	void theDevicesOfAHostApiThatServe() {
		assertEquals("[Mic, Headset] [Speakers, Headset] [Interface]", TGAudioSettings.inputs(this.backend, "MME") + " " + TGAudioSettings.outputs(this.backend, "MME") + " " + TGAudioSettings.outputs(this.backend, "ASIO"));
	}

	@Test
	void noInputDeviceOpensNoInputChannel() {
		TGAudioBackend none = new TestBackend(TestLiveDevice.RAMPS, 0, 0).list(TGAudioBackend.NONE, 0, new TGAudioDeviceInfo(0, "Speakers", "MME", 0, 2, 44100));
		TGAudioSettings settings = new TGAudioSettings("", "", "", 2, 0);
		assertEquals(TGAudioBackend.NONE + " 0", settings.chooseInput(none) + " " + settings.countInputChannels(none, settings.chooseInput(none)));
	}
}
