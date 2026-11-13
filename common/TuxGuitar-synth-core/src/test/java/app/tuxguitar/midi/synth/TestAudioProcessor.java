package app.tuxguitar.midi.synth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Map;

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

	// a processor that reports neither a latency nor a tail has none
	@Test
	public void aProcessorThatReportsNothingHasNoLatencyAndNoTail() {
		TGAudioProcessor plain = new TGAudioProcessor() {
			public void close() {
			}

			public void finalize() {
			}

			public boolean isOpen() {
				return true;
			}

			public boolean isBusy() {
				return false;
			}

			public void fillBuffer(TGAudioBuffer buffer) {
			}

			public void storeParameters(Map<String, String> parameters) {
			}

			public void restoreParameters(Map<String, String> parameters) {
			}
		};
		assertEquals("0 0", plain.getLatency() + " " + plain.getTail());
	}
}
