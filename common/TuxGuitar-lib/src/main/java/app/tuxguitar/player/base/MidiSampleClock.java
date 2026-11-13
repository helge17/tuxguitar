package app.tuxguitar.player.base;

/**
 * The sample clock of a synthesizer that renders audio a block of frames at a time (the TuxGuitar Synthesizer). Before
 * it renders a block it runs its listener, the sequencer playing on it, which sends the block's events: an event sent
 * to the receiver of a frame reaches its instrument on that frame of the block. A port that sends MIDI out has no sample
 * clock; the sequencer then plays on a timer of its own.
 */
public interface MidiSampleClock {

	float getSampleRate();

	// what runs before each block, on the clock's thread; null for nothing
	void setListener(Listener listener);

	// where the events go that reach their instruments on the frame of the block about to be rendered, counted from 0
	MidiReceiver at(int frame);

	interface Listener {

		// the clock is about to render the frames of its next block
		void beforeBlock(MidiSampleClock clock, int frames) throws MidiPlayerException;
	}
}
