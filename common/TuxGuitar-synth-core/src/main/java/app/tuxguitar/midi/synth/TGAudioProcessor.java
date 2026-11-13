package app.tuxguitar.midi.synth;

import java.util.Map;

public interface TGAudioProcessor {

	void close();

	void finalize();

	boolean isOpen();

	boolean isBusy();

	void fillBuffer(TGAudioBuffer buffer);

	void storeParameters(Map<String, String> parameters);

	void restoreParameters(Map<String, String> parameters);

	// the sample rate it renders at, given before its parameters; 44100 Hz unless told
	default void setSampleRate(float sampleRate) {
	}

	// how many frames later than its input (or, for a source, than the events it is sent) it sounds them
	default int getLatency() {
		return 0;
	}
}
