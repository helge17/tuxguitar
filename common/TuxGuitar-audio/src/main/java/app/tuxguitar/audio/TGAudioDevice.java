package app.tuxguitar.audio;

import app.tuxguitar.song.models.TGAudioInput;

/**
 * The audio interface TuxGuitar plays and records through. Its sample clock counts from the start of the song as
 * played; while TuxGuitar is stopped it counts from where the input started delivering.
 */
public interface TGAudioDevice {

	int getSampleRate();

	// what the input channel (from 1) delivered over count samples of the clock, from the given one
	float[] capture(int inputChannel, long from, int count);

	// what the input channel(s) of an audio channel delivered: one side for a mono input, left and right for a pair
	default float[][] capture(TGAudioInput input, long from, int count) {
		float[][] sides = new float[input.countChannels()][];
		for (int side = 0; side < sides.length; side++) {
			sides[side] = this.capture(input.getChannel(side), from, count);
		}
		return sides;
	}
}
