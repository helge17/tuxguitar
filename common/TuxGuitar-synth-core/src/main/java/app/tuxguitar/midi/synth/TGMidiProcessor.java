package app.tuxguitar.midi.synth;

public interface TGMidiProcessor extends TGAudioProcessor {

	void sendNoteOn(int key, int velocity, int voice, boolean bendMode);

	void sendNoteOff(int key, int velocity, int voice, boolean bendMode);

	void sendPitchBend(int value, int voice, boolean bendMode);

	void sendControlChange(int controller, int value);

	// a timed event reaches the instrument on a frame of the processor's next fill, counted from its first frame; a
	// processor that cannot time events sends it at once

	default void sendNoteOn(int key, int velocity, int voice, boolean bendMode, long frame) {
		this.sendNoteOn(key, velocity, voice, bendMode);
	}

	default void sendNoteOff(int key, int velocity, int voice, boolean bendMode, long frame) {
		this.sendNoteOff(key, velocity, voice, bendMode);
	}

	default void sendPitchBend(int value, int voice, boolean bendMode, long frame) {
		this.sendPitchBend(value, voice, bendMode);
	}

	default void sendControlChange(int controller, int value, long frame) {
		this.sendControlChange(controller, value);
	}
}
