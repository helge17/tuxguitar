package app.tuxguitar.midi.synth;

public interface TGMidiProcessor extends TGAudioProcessor {

	void sendNoteOn(int key, int velocity, int voice, boolean bendMode);

	void sendNoteOff(int key, int velocity, int voice, boolean bendMode);

	void sendPitchBend(int value, int voice, boolean bendMode);

	void sendControlChange(int controller, int value);

	// the channel's Bank and Program, given to the source of a custom program: a General MIDI source plays them (with
	// its own soundbank and channel mode); a source that has no programs ignores them
	default void setProgram(int bank, int program) {
	}

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

	// a bend with its exact semitones, which its 7-bit value only comes close to: a source that cannot bend by semitones
	// (Gervill's General MIDI) plays the 7-bit value
	default void sendPitchBend(int value, double semitones, int voice, boolean bendMode, long frame) {
		this.sendPitchBend(value, voice, bendMode, frame);
	}

	default void sendControlChange(int controller, int value, long frame) {
		this.sendControlChange(controller, value);
	}
}
