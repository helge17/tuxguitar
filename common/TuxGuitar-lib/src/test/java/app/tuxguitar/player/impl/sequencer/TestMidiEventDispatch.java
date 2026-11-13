package app.tuxguitar.player.impl.sequencer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiReceiverRecorder;
import app.tuxguitar.player.base.MidiTransmitter;
import app.tuxguitar.util.TGContext;

/**
 * Pins what the sequencer does with an event it sends (MidiEventDispacher.dispatch): channel events reach the
 * receivers of its transmitter, a tempo event changes its tempo, and other events are dropped.
 *
 * A received call is written as MidiReceiverRecorder writes it.
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
		transmitter.addReceiver("recorder", new MidiReceiverRecorder(received));
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

	// through the transmitter (a MIDI Port without a sample clock) a bend is its 7-bit value
	@Test
	public void sendsPitchBendsWithTheirChannelValueVoiceAndBendMode() throws Exception {
		assertEquals("bend 1 64 2 1", send(MidiEvent.pitchBend(0, 0, 1, 64, 0.0, 2, true)));
		assertEquals("bend 3 75 4 0", send(MidiEvent.pitchBend(0, 0, 3, 75, 2.0, 4, false)));
	}

	// a receiver of a block played on the clock that takes a bend's exact semitones: "bend CHANNEL VALUE SEMITONES
	// VOICE BENDMODE"
	private final MidiReceiverRecorder tuned = new MidiReceiverRecorder(received) {
		public void sendPitchBend(int channel, int value, double semitones, int voice, boolean bendMode) {
			received.add("bend " + channel + " " + value + " " + semitones + " " + voice + " " + (bendMode ? 1 : 0));
		}
	};

	// what the sequence holds: the events its handler adds
	private final List<MidiEvent> sequence = new ArrayList<MidiEvent>();

	private final MidiSequenceHandlerImpl handler = new MidiSequenceHandlerImpl(new MidiSequencerImpl(new TGContext()) {
		public void addEvent(MidiEvent event) {
			sequence.add(event);
		}
	}, 3);

	// the bends the handler added, sent on the clock to a receiver that takes their semitones
	private String sentTuned() throws Exception {
		received.clear();
		for (MidiEvent event : sequence) {
			sequencer.sendEvent(event, tuned);
		}
		return String.join(", ", received);
	}

	@Test
	public void aBendOfTheSequenceReachesAReceiverOnTheClockWithItsSemitones() throws Exception {
		handler.addPitchBend(960, 1, 2, 75, 2.0, 3, true);

		assertEquals("bend 2 75 2.0 3 1", sentTuned());
	}

	// a bend added as a 7-bit value alone has the semitones that value plays in the pitch bend range of 12 semitones
	@Test
	public void aBendAddedAsA7BitValueCarriesTheSemitonesItPlays() throws Exception {
		handler.addPitchBend(960, 1, 2, 75, 3, true);
		handler.addPitchBend(960, 1, 2, 32, -1, false);

		assertEquals("bend 2 75 2.0625 3 1, bend 2 32 -6.0 -1 0", sentTuned());
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
}
