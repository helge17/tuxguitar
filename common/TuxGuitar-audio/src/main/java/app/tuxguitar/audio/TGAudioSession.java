package app.tuxguitar.audio;

/**
 * What the audio features keep outside the song: not saved, not undoable, and changing it does not mark the song
 * modified.
 */
public final class TGAudioSession {

	// the MIDI Port of the TuxGuitar Synthesizer (desktop's TGSynthPortProvider), the only one audio tracks sound through
	public static final String SYNTHESIZER_PORT_KEY = "tuxguitar-synth.port";

	private TGAudioSession() {
	}
}
