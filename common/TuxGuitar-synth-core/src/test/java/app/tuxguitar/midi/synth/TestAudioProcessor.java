package app.tuxguitar.midi.synth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * A processor on a block of 32-bit float samples: one that works on the 16-bit buffer processes it through one.
 */
public class TestAudioProcessor {

	@Test
	public void aSixteenBitProcessorProcessesAFloatBlockThroughItsBuffer() {
		float[][] block = new float[2][FakeProcessors.FRAMES];
		Arrays.fill(block[0], 0.25f);
		Arrays.fill(block[1], -0.125f);
		new FakeProcessors.Effect(sample -> sample * 2).process(block);
		assertEquals(0.5f, block[0][0], 0.0001f);
		assertEquals(-0.25f, block[1][FakeProcessors.FRAMES - 1], 0.0001f);
	}
}
