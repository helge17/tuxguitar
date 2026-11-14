package app.tuxguitar.audio;

/**
 * Plays audio faster or slower without changing its pitch.
 */
public interface TGTimeStretcher {

	// the input's sides (one for mono, left and right for stereo) from (included) to (excluded) played over length
	// samples; every side is read at the same places, so the sides stay together
	float[][] stretch(float[][] input, int from, int to, int length);
}
