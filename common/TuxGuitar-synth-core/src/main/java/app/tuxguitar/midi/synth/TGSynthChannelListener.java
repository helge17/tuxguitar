package app.tuxguitar.midi.synth;

/**
 * Hears the processors of a synthesizer's channels change, on the thread that changed them, once they have: a channel's
 * program loaded (again or anew: its processors are those of the program), or its chain closed (a bank select silenced
 * it, or the channel closed: it has no processor).
 */
public interface TGSynthChannelListener {

	void chainChanged(TGSynthChannel channel);
}
