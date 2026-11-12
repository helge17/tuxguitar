package app.tuxguitar.song.models;

/**
 * The input channels of the audio device that an audio channel records from, numbered from 1: one makes mono takes,
 * a pair of two different channels, in either order (left, then right), stereo takes. Immutable, so channels copied
 * from one another share it.
 */
public final class TGAudioInput {

	private final int[] channels;

	private TGAudioInput(int... channels) {
		this.channels = channels;
	}

	public static TGAudioInput mono(int channel) {
		return new TGAudioInput(channel);
	}

	// throws IllegalArgumentException for a pair of one channel
	public static TGAudioInput stereo(int left, int right) {
		if (!isPair(left, right)) {
			throw new IllegalArgumentException("a pair of input channels needs two different channels: " + left + " and " + right);
		}
		return new TGAudioInput(left, right);
	}

	private static boolean isPair(int left, int right) {
		return (left != right);
	}

	// input channels as picked: one is mono, two different ones a pair; null for anything else (none, more than two, a
	// pair of one channel)
	public static TGAudioInput of(int... channels) {
		if (channels.length == 1) {
			return mono(channels[0]);
		}
		return (channels.length == 2 && isPair(channels[0], channels[1]) ? stereo(channels[0], channels[1]) : null);
	}

	public int countChannels() {
		return this.channels.length;
	}

	// the input channel of a side of the takes, from 0: a mono input has side 0 only
	public int getChannel(int side) {
		return this.channels[side];
	}
}
