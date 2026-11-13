package app.tuxguitar.audio.device;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * The audio backend over PortAudio, through the JNI library tuxguitar-audio-device (native/): loaded from a directory,
 * PortAudio initialized once per load and terminated by close (after its streams are closed).
 */
public final class TGPortAudio implements TGAudioBackend {

	static final String LIBRARY = "tuxguitar-audio-device";

	private TGPortAudio() {
	}

	// the library from the directory, PortAudio initialized
	public static TGPortAudio load(File directory) throws TGAudioDeviceException {
		System.load(new File(directory, System.mapLibraryName(LIBRARY)).getAbsolutePath());
		check(nInitialize());
		return new TGPortAudio();
	}

	public List<String> getHostApis() {
		List<String> hostApis = new ArrayList<String>();
		for (int i = 0; i < nHostApiCount(); i++) {
			hostApis.add(nHostApiName(i));
		}
		return hostApis;
	}

	public List<TGAudioDeviceInfo> getDevices() {
		List<TGAudioDeviceInfo> devices = new ArrayList<TGAudioDeviceInfo>();
		for (int i = 0; i < nDeviceCount(); i++) {
			devices.add(new TGAudioDeviceInfo(i, nDeviceName(i), nHostApiName(nDeviceHostApi(i)), nDeviceInputs(i), nDeviceOutputs(i), nDeviceSampleRate(i)));
		}
		return devices;
	}

	public int getDefaultInput() {
		return Math.max(NONE, nDefaultInput());
	}

	public int getDefaultOutput() {
		return Math.max(NONE, nDefaultOutput());
	}

	public TGAudioStream open(int inputDevice, int inputChannels, int outputDevice, int outputChannels, double sampleRate, int frames, TGAudioCallback callback) throws TGAudioDeviceException {
		double rate = (sampleRate != 0 ? sampleRate : nDeviceSampleRate(outputDevice));
		return new TGPortAudioStream(inputChannels, outputChannels, frames, callback, stream -> nOpen(stream, inputDevice, inputChannels, outputDevice, outputChannels, rate, frames, stream.getInput().getBuffer(), stream.getOutput().getBuffer()));
	}

	// PortAudio terminated: every stream must be closed first
	public void close() {
		nTerminate();
	}

	// a PortAudio error (< 0) as its message
	static long check(long result) throws TGAudioDeviceException {
		if (result < 0) {
			throw new TGAudioDeviceException(nErrorText((int) result));
		}
		return result;
	}

	private static native int nInitialize();

	private static native void nTerminate();

	private static native String nErrorText(int error);

	private static native int nHostApiCount();

	private static native String nHostApiName(int index);

	private static native int nDeviceCount();

	private static native String nDeviceName(int index);

	private static native int nDeviceHostApi(int index);

	private static native int nDeviceInputs(int index);

	private static native int nDeviceOutputs(int index);

	private static native double nDeviceSampleRate(int index);

	private static native int nDefaultInput();

	private static native int nDefaultOutput();

	private static native long nOpen(Object stream, int inputDevice, int inputChannels, int outputDevice, int outputChannels, double sampleRate, int frames, ByteBuffer input, ByteBuffer output);

	static native int nStart(long handle);

	static native int nClose(long handle);

	static native double nSampleRate(long handle);

	static native double nInputLatency(long handle);

	static native double nOutputLatency(long handle);
}
