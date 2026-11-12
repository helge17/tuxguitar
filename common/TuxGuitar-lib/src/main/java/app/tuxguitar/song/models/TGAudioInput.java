package app.tuxguitar.song.models;

/**
 * The input channel of the audio device that an audio channel records from, numbered from 1. A channel with one makes
 * mono takes. Immutable, so channels copied from one another share it.
 */
public final class TGAudioInput {

	private final int channel;

	private TGAudioInput(int channel) {
		this.channel = channel;
	}

	public static TGAudioInput mono(int channel) {
		return new TGAudioInput(channel);
	}

	public int getChannel() {
		return this.channel;
	}
}
