package app.tuxguitar.song.models;

/**
 * The input channels of the audio device that an audio channel records from, numbered from 1: one makes mono takes,
 * a pair (left, then right) stereo takes. Immutable, so channels copied from one another share it.
 */
public final class TGAudioInput {

	private final int[] channels;

	private TGAudioInput(int... channels) {
		this.channels = channels;
	}

	public static TGAudioInput mono(int channel) {
		return new TGAudioInput(channel);
	}

	public static TGAudioInput stereo(int left, int right) {
		return new TGAudioInput(left, right);
	}

	public int countChannels() {
		return this.channels.length;
	}

	// the input channel of a side of the takes, from 0: a mono input has side 0 only
	public int getChannel(int side) {
		return this.channels[side];
	}
}
