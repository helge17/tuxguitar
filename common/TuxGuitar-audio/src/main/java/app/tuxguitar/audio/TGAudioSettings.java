package app.tuxguitar.audio;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import app.tuxguitar.audio.device.TGAudioBackend;
import app.tuxguitar.audio.device.TGAudioDeviceInfo;
import app.tuxguitar.util.configuration.TGConfigManager;

/**
 * TuxGuitar's choice of audio devices, kept in its configuration: the host API, the input and the output device by name
 * (none named: the backend's default ones), how many input channels to open from channel 1, and the recording offset
 * (samples added to the input latency the device reports, for drivers that report it wrong).
 */
public final class TGAudioSettings {

	public static final String HOST_API = "audio.host-api";
	public static final String INPUT = "audio.input";
	public static final String OUTPUT = "audio.output";
	public static final String INPUT_CHANNELS = "audio.input-channels";
	public static final String RECORDING_OFFSET = "audio.recording-offset";

	private static final int DEFAULT_INPUT_CHANNELS = 2;
	private static final Predicate<TGAudioDeviceInfo> INPUTS = device -> device.getInputs() > 0;
	private static final Predicate<TGAudioDeviceInfo> OUTPUTS = device -> device.getOutputs() > 0;

	private final String hostApi;
	private final String input;
	private final String output;
	private final int inputChannels;
	private final int recordingOffset;

	public TGAudioSettings(String hostApi, String input, String output, int inputChannels, int recordingOffset) {
		this.hostApi = hostApi;
		this.input = input;
		this.output = output;
		this.inputChannels = inputChannels;
		this.recordingOffset = recordingOffset;
	}

	public static TGAudioSettings read(TGConfigManager config) {
		return new TGAudioSettings(config.getStringValue(HOST_API, ""), config.getStringValue(INPUT, ""), config.getStringValue(OUTPUT, ""),
			config.getIntegerValue(INPUT_CHANNELS, DEFAULT_INPUT_CHANNELS), config.getIntegerValue(RECORDING_OFFSET, 0));
	}

	public void write(TGConfigManager config) {
		config.setValue(HOST_API, this.hostApi);
		config.setValue(INPUT, this.input);
		config.setValue(OUTPUT, this.output);
		config.setValue(INPUT_CHANNELS, this.inputChannels);
		config.setValue(RECORDING_OFFSET, this.recordingOffset);
	}

	public String getHostApi() {
		return this.hostApi;
	}

	public String getInput() {
		return this.input;
	}

	public String getOutput() {
		return this.output;
	}

	public int getInputChannels() {
		return this.inputChannels;
	}

	public int getRecordingOffset() {
		return this.recordingOffset;
	}

	// the output device: the one of that name on the host API that has outputs, else the backend's default output
	public int chooseOutput(TGAudioBackend backend) {
		return this.choose(backend, this.output, OUTPUTS, backend.getDefaultOutput());
	}

	// the input device: the one of that name on the host API that has inputs, else the backend's default input
	public int chooseInput(TGAudioBackend backend) {
		return this.choose(backend, this.input, INPUTS, backend.getDefaultInput());
	}

	private int choose(TGAudioBackend backend, String name, Predicate<TGAudioDeviceInfo> serves, int otherwise) {
		return serving(backend, this.hostApi, serves).filter(device -> device.getName().equals(name)).mapToInt(TGAudioDeviceInfo::getIndex).findFirst().orElse(otherwise);
	}

	// the devices of the host API that have inputs, in the backend's order
	public static List<String> inputs(TGAudioBackend backend, String hostApi) {
		return serving(backend, hostApi, INPUTS).map(TGAudioDeviceInfo::getName).collect(Collectors.toList());
	}

	// the devices of the host API that have outputs, in the backend's order
	public static List<String> outputs(TGAudioBackend backend, String hostApi) {
		return serving(backend, hostApi, OUTPUTS).map(TGAudioDeviceInfo::getName).collect(Collectors.toList());
	}

	private static Stream<TGAudioDeviceInfo> serving(TGAudioBackend backend, String hostApi, Predicate<TGAudioDeviceInfo> serves) {
		return backend.getDevices().stream().filter(device -> device.getHostApi().equals(hostApi) && serves.test(device));
	}

	// the input channels to open on the input device: as many as chosen, at most as many as it has; none without one
	public int countInputChannels(TGAudioBackend backend, int input) {
		return (input == TGAudioBackend.NONE ? 0 : Math.min(this.inputChannels, backend.getDevices().get(input).getInputs()));
	}
}
