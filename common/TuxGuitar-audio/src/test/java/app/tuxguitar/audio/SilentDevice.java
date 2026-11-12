package app.tuxguitar.audio;

/**
 * An audio device at 44100 Hz whose inputs deliver silence and that reports no latency unless a test overrides it.
 */
class SilentDevice implements TGAudioDevice {

	public int getSampleRate() {
		return 44100;
	}

	public float[] capture(int inputChannel, long from, int count) {
		return new float[count];
	}
}
