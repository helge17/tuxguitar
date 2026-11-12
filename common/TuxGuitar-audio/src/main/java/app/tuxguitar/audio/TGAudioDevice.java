package app.tuxguitar.audio;

/**
 * The audio interface TuxGuitar plays and records through. Its sample clock counts from the start of the song as
 * played.
 */
public interface TGAudioDevice {

	int getSampleRate();

	// what the input channel (from 1) delivered over count samples of the clock, from the given one
	float[] capture(int inputChannel, long from, int count);
}
