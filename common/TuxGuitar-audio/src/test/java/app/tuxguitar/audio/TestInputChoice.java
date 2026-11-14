package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGAudioInput;

/**
 * Picking an audio track's input channel(s) in a dialog: the channels offered, the pick as the input channels action
 * takes it (a channel alone is mono, with a second one a stereo pair) and whether it changes the track's input.
 */
class TestInputChoice {

	// the device's channels, and at least those the track records from now (a device with fewer, or none)
	@Test
	void theChannelsOfferedAreTheDevicesAndAtLeastThoseRecordedFromNow() {
		assertEquals("4 4 6 3", TGInputChoice.countChannels(4, TGAudioInput.mono(1)) + " " + TGInputChoice.countChannels(4, TGAudioInput.stereo(4, 2))
			+ " " + TGInputChoice.countChannels(2, TGAudioInput.stereo(1, 6)) + " " + TGInputChoice.countChannels(0, TGAudioInput.mono(3)));
	}

	@Test
	void aChannelAloneIsMonoWithASecondAPair() {
		assertEquals("[3] [4, 2]", Arrays.toString(TGInputChoice.pick(3, TGInputChoice.NONE)) + " " + Arrays.toString(TGInputChoice.pick(4, 2)));
	}

	// the second channel of an input, as the dialog shows it first: none for a mono input
	@Test
	void theSecondChannelOfAnInputIsNoneWhenMono() {
		assertEquals(TGInputChoice.NONE + " 2", TGInputChoice.second(TGAudioInput.mono(3)) + " " + TGInputChoice.second(TGAudioInput.stereo(4, 2)));
	}

	@Test
	void aPickChangesTheInputUnlessItIsTheSameChannelsInTheSameOrder() {
		TGAudioInput pair = TGAudioInput.stereo(1, 2);
		assertEquals("false true true true", TGInputChoice.changes(pair, TGInputChoice.pick(1, 2)) + " " + TGInputChoice.changes(pair, TGInputChoice.pick(2, 1))
			+ " " + TGInputChoice.changes(pair, TGInputChoice.pick(1, TGInputChoice.NONE)) + " " + TGInputChoice.changes(TGAudioInput.mono(1), TGInputChoice.pick(1, 2)));
	}
}
