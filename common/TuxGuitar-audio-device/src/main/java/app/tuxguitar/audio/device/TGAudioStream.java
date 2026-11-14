package app.tuxguitar.audio.device;

/**
 * An open stream on an audio device: the sample rate it runs at, the block of frames its callback is given, and the
 * latencies the device reports for it, in frames: how long after the callback reads an input frame it was played into
 * the device, and how long after the callback writes an output frame the device sounds it (the backend's own buffering
 * included). Started, the device runs the callback; closed, it never runs it again.
 */
public interface TGAudioStream extends AutoCloseable {

	int getSampleRate();

	int getBlock();

	int getInputLatency();

	int getOutputLatency();

	void start() throws TGAudioDeviceException;

	// stops the device once its last callback has returned; closing again does nothing
	void close();
}
