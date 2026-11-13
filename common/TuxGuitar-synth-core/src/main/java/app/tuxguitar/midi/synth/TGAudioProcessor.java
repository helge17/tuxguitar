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

	// a block of 32-bit float samples (left and right, a buffer's frames) through the processor, in place. A processor
	// that works on floats keeps what goes above full scale, so a chain of them has room there; by default the block
	// goes through the 16-bit buffer
	default void process(float[][] block) {
		TGAudioBuffer buffer = new TGAudioBuffer();
		buffer.write(block);
		this.fillBuffer(buffer);
		buffer.read(block);
	}

	// how many frames later than its input (or, for a source, than the events it is sent) it sounds them
	default int getLatency() {
		return 0;
	}
}
