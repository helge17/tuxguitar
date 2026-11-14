package app.tuxguitar.audio;

import java.util.Arrays;
import java.util.stream.IntStream;

import app.tuxguitar.song.models.TGAudioInput;

/**
 * Picking an audio track's input channel(s), numbered from 1, as a dialog does: a first channel and a second one, or
 * none; a channel alone records mono takes, with a second one a stereo pair (left, then right; TGSetInputChannelsAction
 * refuses a pair of one channel).
 */
public final class TGInputChoice {

	// no second channel: mono
	public static final int NONE = 0;

	private TGInputChoice() {
	}

	// how many channels to offer: the device's, and at least as many as the input records from now (a device with
	// fewer, or none, does not take them away)
	public static int countChannels(int deviceChannels, TGAudioInput input) {
		return Math.max(deviceChannels, Arrays.stream(channels(input)).max().getAsInt());
	}

	// the channels picked, as TGSetInputChannelsAction takes them
	public static int[] pick(int first, int second) {
		return (second == NONE ? new int[] {first} : new int[] {first, second});
	}

	// the second channel of the input; none when mono
	public static int second(TGAudioInput input) {
		return (input.countChannels() > 1 ? input.getChannel(1) : NONE);
	}

	// whether the channels picked are other than the input's (the same channels in another order are)
	public static boolean changes(TGAudioInput input, int[] picked) {
		return !Arrays.equals(channels(input), picked);
	}

	private static int[] channels(TGAudioInput input) {
		return IntStream.range(0, input.countChannels()).map(input::getChannel).toArray();
	}
}
