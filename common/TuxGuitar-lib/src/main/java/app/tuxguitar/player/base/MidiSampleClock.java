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

	// what the song does over the block about to be rendered, as its listener tells it; null when it has none
	default MidiTransport getTransport() {
		return null;
	}

	// how many frames after the frame an event is sent on the output sounds it (the synthesizer's chains, then the audio
	// device's output latency): a sequencer playing on the clock reports the position that many frames behind
	default int getSoundingDelay() {
		return 0;
	}

	// the channel of the clock's own General MIDI drums (see hasDrums): no song channel has this id
	int DRUMS = -2;

	// whether the clock has General MIDI drums of its own, outside every channel's sound chain, opened as the channel
	// DRUMS: while the song plays on the clock the Metronome clicks there, rather than on the song's percussion channel
	default boolean hasDrums() {
		return false;
	}

	interface Listener {

		// the clock is about to render the frames of its next block
		void beforeBlock(MidiSampleClock clock, int frames) throws MidiPlayerException;
	}
}
