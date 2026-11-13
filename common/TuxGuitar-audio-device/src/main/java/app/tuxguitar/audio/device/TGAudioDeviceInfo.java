package app.tuxguitar.audio.device;

/**
 * An audio device as its host API lists it: its index among the backend's devices, its name, the host API it belongs
 * to (ASIO, Windows WASAPI, Core Audio, ALSA...), how many input and output channels it has, and the sample rate it
 * runs at by default. Immutable.
 */
public final class TGAudioDeviceInfo {

	private final int index;
	private final String name;
	private final String hostApi;
	private final int inputs;
	private final int outputs;
	private final double sampleRate;

	public TGAudioDeviceInfo(int index, String name, String hostApi, int inputs, int outputs, double sampleRate) {
		this.index = index;
		this.name = name;
		this.hostApi = hostApi;
		this.inputs = inputs;
		this.outputs = outputs;
		this.sampleRate = sampleRate;
	}

	public int getIndex() {
		return this.index;
	}

	public String getName() {
		return this.name;
	}

	public String getHostApi() {
		return this.hostApi;
	}

	public int getInputs() {
		return this.inputs;
	}

	public int getOutputs() {
		return this.outputs;
	}

	public double getSampleRate() {
		return this.sampleRate;
	}

	public String toString() {
		return this.hostApi + ": " + this.name + " (" + this.inputs + " in, " + this.outputs + " out, " + this.sampleRate + " Hz)";
	}
}
