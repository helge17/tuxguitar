package app.tuxguitar.midi.synth;

import java.util.Map;

import app.tuxguitar.player.base.MidiTransport;

public interface TGAudioProcessor {

	// the tail of a processor that sounds on for ever
	int INFINITE_TAIL = Integer.MAX_VALUE;

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

	// what the song does over each block it processes, written before the block by the clock's owner; a processor that
	// does not need it ignores it
	default void setTransport(MidiTransport transport) {
	}

	// how many frames later than its input (or, for a source, than the events it is sent) it sounds them
	default int getLatency() {
		return 0;
	}

	// how many frames it sounds on after its input stops (or, for a source, after its last note ends), as it reports;
	// INFINITE_TAIL when it never stops
	default int getTail() {
		return 0;
	}

	// frames along a chain (latencies, tails) add up; an endless tail stays endless
	static int along(int frames, int more) {
		return (int) Math.min(INFINITE_TAIL, (long) frames + more);
	}
}
