package app.tuxguitar.player.base;

public interface MidiReceiver {

	public void sendNoteOn(int channel, int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException;

	public void sendNoteOff(int channel, int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException;

	// a bend as its 7-bit value (64 none; the pitch bend range is 12 semitones)
	public void sendPitchBend(int channel, int value, int voice, boolean bendMode) throws MidiPlayerException;

	// a bend as its exact semitones too, which the 7-bit value only comes close to; a receiver that sends MIDI on keeps
	// the 7-bit value
	default void sendPitchBend(int channel, int value, double semitones, int voice, boolean bendMode) throws MidiPlayerException {
		this.sendPitchBend(channel, value, voice, bendMode);
	}

	public void sendProgramChange(int channel, int value) throws MidiPlayerException;

	public void sendControlChange(int channel, int controller, int value) throws MidiPlayerException;

	public void sendParameter(int channelId, String key, String value) throws MidiPlayerException;

	public void sendAllNotesOff() throws MidiPlayerException;

	public void sendPitchBendReset() throws MidiPlayerException;

}
