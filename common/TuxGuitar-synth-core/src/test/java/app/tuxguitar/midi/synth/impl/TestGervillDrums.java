package app.tuxguitar.midi.synth.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiClick;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGPropertiesManager;

/**
 * The General MIDI drums Count In and the Metronome click on, headless: a click's note reaches the drums on its own
 * frame, inside a block, and sounds its drum note at its velocity. A drum's attack is not a click, so where a note
 * reaches the drums is measured by moving it: the sound moves with it, sample for sample.
 */
public class TestGervillDrums {

	private static final int BLOCKS = 40;
	// above this a sample is sounding: -60 dBFS
	private static final float SOUNDING = 0.001f;

	private final TGContext context = new TGContext();

	public TestGervillDrums() {
		TGPropertiesManager.getInstance(this.context).setPropertiesFactory(TestGervillProcessor.MapProperties::new);
	}

	// the left side of the drums sounding the clicks, each at its frame and held for a beat of 10000 frames
	private float[] render(float sampleRate, MidiClick click, long... frames) {
		return held(sampleRate, click, 10000, frames);
	}

	private float[] held(float sampleRate, MidiClick click, long length, long... frames) {
		GervillDrums drums = new GervillDrums(this.context, sampleRate);
		for (long frame : frames) {
			drums.click(click, frame, length);
		}
		float[] left = new float[BLOCKS * GervillDrums.FRAMES];
		float[][] block = new float[2][GervillDrums.FRAMES];
		for (int b = 0; b < BLOCKS; b++) {
			drums.fill(block);
			System.arraycopy(block[0], 0, left, b * GervillDrums.FRAMES, GervillDrums.FRAMES);
		}
		drums.close();
		return left;
	}

	private static int firstSounding(float[] samples) {
		int first = 0;
		while (first < samples.length && Math.abs(samples[first]) <= SOUNDING) {
			first++;
		}
		return first;
	}

	@Test
	public void aClickIsSilentBeforeItsFrameAndSoundsFromIt() {
		float[] left = render(44100, MidiClick.DEFAULT, 1000);

		assertEquals(0, TestGervillProcessor.peak(Arrays.copyOfRange(left, 0, 1000)), 0);
		assertTrue(TestGervillProcessor.peak(Arrays.copyOfRange(left, 1000, 23050)) > SOUNDING);
	}

	// moved by 137 frames, inside a block and across Gervill's control periods, the sound starts 137 frames later
	@Test
	public void aClickReachesTheDrumsOnItsFrameInsideABlock() {
		for (float rate : new float[] {44100, 48000}) {
			int early = firstSounding(render(rate, MidiClick.DEFAULT, 1000));
			int late = firstSounding(render(rate, MidiClick.DEFAULT, 1137));

			assertEquals(137, late - early, rate + " Hz: " + early + ", " + late);
		}
	}

	// a second click, many blocks on: what it adds starts as far after its frame as the first click's sound does
	@Test
	public void aLaterClickReachesTheDrumsOnItsFrame() {
		float[] one = render(44100, MidiClick.DEFAULT, 1000);
		float[] two = render(44100, MidiClick.DEFAULT, 1000, 13137);
		float[] added = new float[one.length];
		for (int i = 0; i < added.length; i++) {
			added[i] = two[i] - one[i];
		}

		assertEquals(firstSounding(one) - 1000, firstSounding(added) - 13137);
	}

	@Test
	public void aClickSoundsItsNoteAtItsVelocity() {
		float[] sideStick = render(44100, MidiClick.DEFAULT, 0);
		float[] loudSideStick = render(44100, new MidiClick(37, 127), 0);
		float[] closedHiHat = render(44100, new MidiClick(42, 127), 0);

		assertTrue(TestGervillProcessor.peak(loudSideStick) > TestGervillProcessor.peak(sideStick), TestGervillProcessor.peak(loudSideStick) + " > " + TestGervillProcessor.peak(sideStick));
		assertTrue(!Arrays.equals(loudSideStick, closedHiHat) && TestGervillProcessor.peak(closedHiHat) > SOUNDING, "a closed hi-hat, not a side stick");
	}

	// released after 1000 frames, a click rings out sooner than one held for 10000
	@Test
	public void aClickIsReleasedAfterItsLength() {
		float[] held = held(44100, MidiClick.DEFAULT, 10000, 0);
		float[] released = held(44100, MidiClick.DEFAULT, 1000, 0);

		double heldTail = TestGervillProcessor.peak(Arrays.copyOfRange(held, 3000, 10000));
		double releasedTail = TestGervillProcessor.peak(Arrays.copyOfRange(released, 3000, 10000));
		assertTrue(releasedTail < heldTail, releasedTail + " < " + heldTail);
	}
}
