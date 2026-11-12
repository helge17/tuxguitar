package app.tuxguitar.audio;

/**
 * Plays audio faster or slower without changing its pitch.
 */
public interface TGTimeStretcher {

	// the input from (included) to (excluded) played over length samples
	float[] stretch(float[] input, int from, int to, int length);
}
