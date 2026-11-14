package app.tuxguitar.audio.device;

import java.util.List;

/**
 * The native audio library TuxGuitar plays and records through: its host APIs, the devices they list, and streams on
 * them. A stream opens input channels 1 to inputChannels of the input device (none: no input) and the first
 * outputChannels of the output device, at the sample rate (0: the output device's own), and runs its callback in
 * blocks of frames.
 */
public interface TGAudioBackend {

	// none: no input device, no default device
	int NONE = -1;

	List<String> getHostApis();

	List<TGAudioDeviceInfo> getDevices();

	int getDefaultInput();

	int getDefaultOutput();

	TGAudioStream open(int inputDevice, int inputChannels, int outputDevice, int outputChannels, double sampleRate, int frames, TGAudioCallback callback) throws TGAudioDeviceException;
}
