package app.tuxguitar.midi.synth;

import static app.tuxguitar.midi.synth.FakeProcessors.level;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.util.TGContext;

/**
 * Pins the synthesizer's audio between processors today: 512 frames of 16-bit little-endian stereo at 44100 Hz, mixed by
 * averaging and scaled back by the number of buffers mixed.
 */
public class TestAudioBuffer {

	private static final float STEP = 1f / Short.MAX_VALUE;

	private static float[] firstFrame(TGAudioBuffer buffer) {
		float[][] samples = FakeProcessors.read(buffer);
		return new float[] {samples[0][0], samples[1][0], samples[0][FakeProcessors.FRAMES - 1], samples[1][FakeProcessors.FRAMES - 1]};
	}

	@Test
	public void aBufferHolds512FramesOf16BitStereo() {
		TGAudioBuffer buffer = new TGAudioBuffer();
		assertEquals(2048, buffer.getLength());
		assertEquals(2048, buffer.getBuffer().length);
		assertEquals(44100f, TGAudioBuffer.SAMPLE_RATE, 0f);
	}

	// the last write of a float chain into 16 bits: what is above full scale saturates there, it does not wrap around
	@Test
	public void writingThenReadingRoundsTowardZeroToA16BitStepAndSaturatesAboveFullScale() {
		float[] above = firstFrame(level(1.5f, -1.5f));
		assertEquals(1f, above[0], 0f);
		assertEquals(-32768 * STEP, above[1], 0f);
		float[] frame = firstFrame(level(0.5f, -0.5f));
		assertEquals(16383 * STEP, frame[0], 0f);
		assertEquals(-16383 * STEP, frame[1], 0f);
		assertEquals(16383 * STEP, frame[2], 0f);
		assertEquals(-16383 * STEP, frame[3], 0f);
	}

	@Test
	public void samplesAreLittleEndian() {
		byte[] bytes = level(256 * STEP + STEP / 2, 0f).getBuffer();
		assertEquals(0, bytes[0]);
		assertEquals(1, bytes[1]);
	}

	@Test
	public void clearingSilencesTheBuffer() {
		TGAudioBuffer buffer = level(0.5f, 0.5f);
		buffer.clear();
		assertEquals(0f, firstFrame(buffer)[0], 0f);
		assertEquals(0f, firstFrame(buffer)[3], 0f);
	}

	@Test
	public void writingABufferCopiesIt() {
		TGAudioBuffer buffer = new TGAudioBuffer();
		buffer.write(level(0.25f, -0.125f));
		assertEquals(8191 * STEP, firstFrame(buffer)[0], 0f);
		assertEquals(-4095 * STEP, firstFrame(buffer)[3], 0f);
	}

	@Test
	public void writingBytesOfAnotherLengthChangesNothing() {
		TGAudioBuffer buffer = level(0.25f, 0.25f);
		buffer.write(new byte[16]);
		buffer.mix(new byte[16]);
		assertEquals(8191 * STEP, firstFrame(buffer)[0], 0f);
	}

	@Test
	public void oneMixedBufferIsCopiedAndClippingLeavesIt() {
		TGAudioBuffer buffer = new TGAudioBuffer();
		buffer.clear();
		buffer.mix(level(0.25f, -0.25f));
		buffer.clip();
		assertEquals(8191 * STEP, firstFrame(buffer)[0], 0f);
		assertEquals(-8191 * STEP, firstFrame(buffer)[1], 0f);
	}

	@Test
	public void mixedBuffersAreAveragedAndClippingScalesThemBackToTheirSum() {
		TGAudioBuffer buffer = new TGAudioBuffer();
		buffer.clear();
		buffer.mix(level(0.25f, 0.25f));
		buffer.mix(level(0.125f, -0.25f));
		float[] averaged = firstFrame(buffer);
		assertEquals(0.1875f, averaged[0], 2 * STEP);
		assertEquals(0f, averaged[1], 2 * STEP);
		buffer.clip();
		float[] summed = firstFrame(buffer);
		assertEquals(0.375f, summed[0], 3 * STEP);
		assertEquals(0f, summed[1], 3 * STEP);
		assertEquals(0.375f, summed[2], 3 * STEP);
	}

	@Test
	public void clippingSaturatesAtFullScale() {
		TGAudioBuffer buffer = new TGAudioBuffer();
		buffer.clear();
		buffer.mix(level(0.75f, -0.75f));
		buffer.mix(level(0.75f, -0.75f));
		buffer.clip();
		float[] frame = firstFrame(buffer);
		assertEquals(1f, frame[0], 0f);
		assertEquals(-32768 * STEP, frame[1], 0f);
	}

	@Test
	public void balanceScalesEachSideAndSaturates() {
		TGAudioBuffer buffer = level(0.75f, -0.75f);
		buffer.balance(new float[] {2f, 0.5f});
		float[] frame = firstFrame(buffer);
		assertEquals(1f, frame[0], 0f);
		assertEquals(-12287 * STEP, frame[1], STEP);
		assertEquals(1f, frame[2], 0f);
	}

	// odd, pinned: with fewer volumes than sides the position does not move past the side left out, so the volumes
	// apply to the first samples of the buffer, whatever side they are on, and the last samples are left as they were
	@Test
	public void balanceWithOneVolumeScalesTheFirstHalfOfTheSamples() {
		TGAudioBuffer buffer = level(0.5f, 0.5f);
		buffer.balance(new float[] {0f});
		float[] frame = firstFrame(buffer);
		assertEquals(0f, frame[0], 0f);
		assertEquals(0f, frame[1], 0f);
		assertEquals(16383 * STEP, frame[2], 0f);
		assertEquals(16383 * STEP, frame[3], 0f);
	}

	@Test
	public void balanceWithMoreVolumesThanSidesLeavesTheRestOut() {
		TGAudioBuffer buffer = level(0.5f, 0.5f);
		buffer.balance(new float[] {0.5f, 0.5f, 0f});
		float[] frame = firstFrame(buffer);
		assertEquals(8191 * STEP, frame[0], STEP);
		assertEquals(8191 * STEP, frame[1], STEP);
		assertEquals(8191 * STEP, frame[2], STEP);
	}

	@Test
	public void readingIntoOneSideKeepsTheRightSide() {
		float[][] mono = new float[1][FakeProcessors.FRAMES];
		level(0.5f, 0.25f).read(mono);
		assertEquals(8191 * STEP, mono[0][0], 0f);
	}

	@Test
	public void writingOneSideFillsBoth() {
		float[][] mono = new float[1][FakeProcessors.FRAMES];
		java.util.Arrays.fill(mono[0], 0.5f);
		TGAudioBuffer buffer = new TGAudioBuffer();
		buffer.write(mono);
		float[] frame = firstFrame(buffer);
		assertEquals(16383 * STEP, frame[0], 0f);
		assertEquals(16383 * STEP, frame[3], 0f);
	}

	@Test
	public void theBufferProcessorSumsTheChannelsOfTheSynthesizer() throws MidiPlayerException {
		TGContext context = new TGContext();
		TGSynthManager.getInstance(context).addExtension(TGMidiProcessorFactory.class, new FakeProcessors.SourceFactory("source", 0.25f));
		TGSynthModel model = new TGSynthModel(context);
		model.getProgram(0, 0).copyFrom(FakeProcessors.program("source"));
		for (int id = 1; id <= 2; id++) {
			TGSynthChannel channel = (TGSynthChannel) model.openChannel(id);
			channel.sendControlChange(app.tuxguitar.player.base.MidiControllers.BALANCE, 0);
			channel.sendProgramChange(0);
		}
		TGAudioBufferProcessor processor = new TGAudioBufferProcessor(model);
		processor.process();
		float[] frame = firstFrame(processor.getBuffer());
		assertEquals(0.5f, frame[0], 3 * STEP);
		assertEquals(0f, frame[1], 3 * STEP);
	}
}
