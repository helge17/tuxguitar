package app.tuxguitar.player.impl.sequencer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiReceiverRecorder;
import app.tuxguitar.player.base.MidiSequenceHandler;
import app.tuxguitar.player.base.MidiTransmitter;
import app.tuxguitar.util.TGContext;

/**
 * Pins one step of the sequencer's timer (MidiSequencerImpl.process): a running sequencer first silences what was
 * playing when it was started or moved, then sends the events reached, and stops once it is past its last event; a
 * sequencer that has just stopped silences what was playing and forgets its events; a stopped one does nothing.
 *
 * No thread handler is installed, so the timer itself does not run: each test steps it. The sequence has a note at
 * tick 0 and one at tick 500, its last event. A step is written "what the receiver got | returns whether the
 * sequencer was running | whether it runs now"; a received call is written as MidiReceiverRecorder writes it.
 */
public class TestMidiSequencerProcess {

	private final List<String> received = new ArrayList<String>();
	private final MidiSequencerImpl sequencer = new MidiSequencerImpl(new TGContext());

	public TestMidiSequencerProcess() throws MidiPlayerException {
		MidiTransmitter transmitter = new MidiTransmitter();
		transmitter.addReceiver("recorder", new MidiReceiverRecorder(received));
		sequencer.setTransmitter(transmitter);
		MidiSequenceHandler sequence = sequencer.createSequence(1);
		sequence.addNoteOn(0, 0, 1, 60, 100, 1, false);
		sequence.addNoteOn(500, 0, 1, 62, 100, 1, false);
	}

	// starts at the position; the clock then stands still, so only a move changes the position
	private void startAt(long position) throws MidiPlayerException {
		sequencer.start();
		sequencer.setTempo(0);
		sequencer.setTickPosition(position);
	}

	private String step() throws MidiPlayerException {
		received.clear();
		boolean wasRunning = sequencer.process();
		return String.join(", ", received) + " | " + wasRunning + " | " + sequencer.isRunning();
	}

	@Test
	public void aStoppedSequencerDoesNothing() throws MidiPlayerException {
		assertEquals(" | false | false", step());
	}

	@Test
	public void aStartedSequencerFirstSilencesWhatPlayedThenSendsTheEventsReachedUpToTheLastWithoutStopping()
			throws MidiPlayerException {
		startAt(0);
		String atTheStart = step();
		startAt(500);

		assertEquals("all notes off, pitch bend reset, on 1 60 100 1 0 | true | true"
				+ " / all notes off, pitch bend reset, on 1 62 100 1 0 | true | true", atTheStart + " / " + step());
	}

	@Test
	public void aRunningSequencerSilencesAgainOnlyWhenMovedThenSendsTheEventsFromThere() throws MidiPlayerException {
		startAt(300);
		step();
		String unmoved = step();
		sequencer.setTickPosition(0);

		assertEquals(" | true | true / all notes off, pitch bend reset, on 1 60 100 1 0 | true | true", unmoved + " / " + step());
	}

	@Test
	public void pastTheLastEventTheSequencerStopsSilencesAndForgetsItsEvents() throws MidiPlayerException {
		startAt(501);
		String passing = step();
		String stopped = step();
		startAt(0);

		assertEquals("all notes off, pitch bend reset, all notes off, pitch bend reset | true | false"
				+ " /  | false | false / all notes off, pitch bend reset | true | true",
				passing + " / " + stopped + " / " + step());
	}

	@Test
	public void stoppingSilencesWhatPlayedOnce() throws MidiPlayerException {
		startAt(0);
		step();
		received.clear();
		sequencer.stop();
		String stopping = String.join(", ", received);

		assertEquals("all notes off, pitch bend reset /  | false | false", stopping + " / " + step());
	}
}
