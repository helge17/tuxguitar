package app.tuxguitar.player.base;

import java.util.List;

/**
 * A MidiReceiver that writes each call it receives as one line, "kind a b c ..." in argument order; bend mode is
 * written 0/1; a recorder made with a prefix writes it before each line.
 */
public class MidiReceiverRecorder implements MidiReceiver {

	private final List<String> received;
	private final String prefix;

	public MidiReceiverRecorder(List<String> received) {
		this(received, "");
	}

	public MidiReceiverRecorder(List<String> received, String prefix) {
		this.received = received;
		this.prefix = prefix;
	}

	private void record(String kind, int... values) {
		StringBuilder call = new StringBuilder(this.prefix).append(kind);
		for (int value : values) {
			call.append(' ').append(value);
		}
		received.add(call.toString());
	}

	private int bit(boolean value) {
		return value ? 1 : 0;
	}

	public void sendNoteOn(int channel, int key, int velocity, int voice, boolean bendMode) {
		record("on", channel, key, velocity, voice, bit(bendMode));
	}

	public void sendNoteOff(int channel, int key, int velocity, int voice, boolean bendMode) {
		record("off", channel, key, velocity, voice, bit(bendMode));
	}

	public void sendPitchBend(int channel, int value, int voice, boolean bendMode) {
		record("bend", channel, value, voice, bit(bendMode));
	}

	public void sendProgramChange(int channel, int value) {
		record("program", channel, value);
	}

	public void sendControlChange(int channel, int controller, int value) {
		record("cc", channel, controller, value);
	}

	public void sendParameter(int channelId, String key, String value) {
		received.add("parameter " + channelId + " " + key + "=" + value);
	}

	public void sendAllNotesOff() {
		received.add("all notes off");
	}

	public void sendPitchBendReset() {
		received.add("pitch bend reset");
	}
}
