package app.tuxguitar.player.impl.sequencer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiReceiver;
import app.tuxguitar.player.base.MidiTransmitter;
import app.tuxguitar.util.TGContext;

/**
 * Pins what the sequencer does with an event it sends (MidiEventDispacher.dispatch): channel events reach the
 * receivers of its transmitter, a tempo event changes its tempo, and other events are dropped.
 *
 * A received call is written "kind a b c ...", in argument order; bend mode is written 0/1.
 */
public class TestMidiEventDispatch {

	private final List<String> received = new ArrayList<String>();

	private final MidiSequencerImpl sequencer = new MidiSequencerImpl(new TGContext()) {
		public void setTempo(int tempo) {
			received.add("tempo " + tempo);
		}
	};

	public TestMidiEventDispatch() {
		MidiTransmitter transmitter = new MidiTransmitter();
		transmitter.addReceiver("recorder", new Recorder());
		sequencer.setTransmitter(transmitter);
	}

	private String send(MidiEvent event) throws Exception {
		received.clear();
		sequencer.sendEvent(event);
		return String.join(", ", received);
	}

	@Test
	public void sendsNotesWithTheirChannelKeyVelocityVoiceAndBendMode() throws Exception {
		assertEquals("on 1 60 100 2 1", send(MidiEvent.noteOn(0, 0, 1, 60, 100, 2, true)));
		assertEquals("on 3 61 101 4 0", send(MidiEvent.noteOn(0, 0, 3, 61, 101, 4, false)));
		assertEquals("off 5 62 102 6 0", send(MidiEvent.noteOff(0, 0, 5, 62, 102, 6, false)));
		assertEquals("off 7 63 103 8 1", send(MidiEvent.noteOff(0, 0, 7, 63, 103, 8, true)));
	}

	@Test
	public void sendsPitchBendsWithTheirChannelValueVoiceAndBendMode() throws Exception {
		assertEquals("bend 1 64 2 1", send(MidiEvent.pitchBend(0, 0, 1, 64, 2, true)));
		assertEquals("bend 3 65 4 0", send(MidiEvent.pitchBend(0, 0, 3, 65, 4, false)));
	}

	@Test
	public void sendsControlAndProgramChanges() throws Exception {
		assertEquals("cc 1 7 90", send(MidiEvent.controlChange(0, 0, 1, 7, 90)));
		assertEquals("program 2 25", send(MidiEvent.programChange(0, 0, 2, 25)));
	}

	@Test
	public void aTempoEventSetsTheTempoInBeatsPerMinute() throws Exception {
		// 500000 microseconds per quarter = 0x07A120: every byte counts, one has its high bit set
		assertEquals("tempo 120", send(MidiEvent.tempoInUSQ(0, 500000)));
		assertEquals("tempo 60", send(MidiEvent.tempoInUSQ(0, 1000000)));
	}

	@Test
	public void otherEventsAreDropped() throws Exception {
		assertEquals("", send(new MidiEvent(0, MidiEvent.MIDI_SYSTEM_EVENT, new byte[] {0x2F, 0x20, (byte) 0xA1, 0x07})));
		assertEquals("", send(MidiEvent.trackName(0, 0, "name")));
	}

	private class Recorder implements MidiReceiver {

		private void record(String kind, int... values) {
			StringBuilder call = new StringBuilder(kind);
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
}
