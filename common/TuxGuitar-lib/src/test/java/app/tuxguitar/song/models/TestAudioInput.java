package app.tuxguitar.song.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * An audio input is one input channel or a pair of two different ones, in either order (left, then right). Input
 * channels picked by the user are parsed once into one: what is neither reads "none". An input reads as its channels
 * joined by "+".
 */
public class TestAudioInput {

	private static String read(TGAudioInput input) {
		if (input == null) {
			return "none";
		}
		return (input.countChannels() == 1 ? "" + input.getChannel(0) : input.getChannel(0) + "+" + input.getChannel(1));
	}

	@Test
	public void aPairIsAnyTwoDifferentChannelsInEitherOrder() {
		assertEquals("3+7", read(TGAudioInput.of(3, 7)));
		assertEquals("2+1", read(TGAudioInput.of(2, 1)));
	}

	@Test
	public void oneChannelIsMono() {
		assertEquals("2", read(TGAudioInput.of(2)));
	}

	@Test
	public void aPairOfOneChannelNoChannelOrMoreThanTwoAreNoInput() {
		assertEquals("none", read(TGAudioInput.of(1, 1)));
		assertEquals("none", read(TGAudioInput.of()));
		assertEquals("none", read(TGAudioInput.of(1, 2, 3)));
	}

	@Test
	public void aStereoInputOfOneChannelCannotBeMade() {
		assertThrows(IllegalArgumentException.class, () -> TGAudioInput.stereo(4, 4));
	}
}
