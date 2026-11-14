package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/**
 * The player starts its sequencer on the sample clock of the output port's synthesizer when it has one (the TuxGuitar
 * Synthesizer), so the song's events reach it on their frames; on any other port the sequencer plays on its own timer,
 * as it always has.
 *
 * The song plays through a FakeSequencer (see PlayingFixture), its threads run step by step.
 */
public class TestMidiPlayerClock {

	private final PlayingFixture fixture = new PlayingFixture();
	private final MidiPlayer player = fixture.player;

	private static class Synthesizer implements MidiSynthesizer {
		public MidiChannel openChannel(int channelId) { return null; }
		public void closeChannel(MidiChannel midiChannel) { }
		public boolean isChannelOpen(MidiChannel midiChannel) { return false; }
		public boolean isBusy() { return false; }
	}

	private static class ClockedSynthesizer extends Synthesizer implements MidiSampleClock {
		public float getSampleRate() { return 44100; }
		public void setListener(Listener listener) { }
		public MidiReceiver at(int frame) { return null; }
	}

	// plays through the synthesizer, the start run
	private void playThrough(MidiSynthesizer synthesizer) throws MidiPlayerException {
		player.getSynthesizerProxy().setMidiSynthesizer(synthesizer);
		player.play();
		fixture.threads.runStarted();
		fixture.threads.runLoop();
	}

	@Test
	public void theSequencerStartsOnTheSampleClockOfTheSynthesizer() throws MidiPlayerException {
		ClockedSynthesizer synthesizer = new ClockedSynthesizer();
		playThrough(synthesizer);

		assertSame(synthesizer, fixture.sequencer.startedOn);
	}

	@Test
	public void throughASynthesizerWithoutASampleClockTheSequencerStartsOnItsTimer() throws MidiPlayerException {
		playThrough(new ClockedSynthesizer());
		player.stop();
		playThrough(new Synthesizer());

		assertNull(fixture.sequencer.startedOn);
	}
}
