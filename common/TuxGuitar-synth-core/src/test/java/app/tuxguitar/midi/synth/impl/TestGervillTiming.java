package app.tuxguitar.midi.synth.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.midi.synth.TGAudioBufferProcessor;
import app.tuxguitar.midi.synth.TGMidiProcessorFactory;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.midi.synth.TGSynthChannel;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.midi.synth.TGSynthModel;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGPropertiesManager;

/**
 * When a note reaches a channel of the TuxGuitar Synthesizer playing live, block by block as its audio thread renders
 * it: General MIDI program 80 (a square lead) on Gervill at 44100 Hz. A note's attack is not a click, so where a note
 * starts is measured against the same note sent before the first block: the sound moves with it, sample for sample.
 * Where the stream is silent is measured exactly: Gervill writes 0 there.
 */
public class TestGervillTiming {

	private static final int FRAMES = TGAudioBuffer.BUFFER_SIZE / 2;
	private static final int BLOCKS = 12;
	// above this a sample is sounding: -60 dBFS
	private static final float SOUNDING = 0.001f;
	private static final int CHANNEL = 1;

	private final TGContext context = new TGContext();
	private final TGSynthModel model;
	private final TGAudioBufferProcessor blocks;

	public TestGervillTiming() throws MidiPlayerException {
		TGPropertiesManager.getInstance(this.context).setPropertiesFactory(TestGervillProcessor.MapProperties::new);
		TGSynthManager.getInstance(this.context).addExtension(TGMidiProcessorFactory.class, new GervillProcessorFactory(this.context));
		this.model = new TGSynthModel(this.context);
		TGProgramElement receiver = new TGProgramElement();
		receiver.setId("gm");
		receiver.setType(GervillProcessorFactory.TYPE);
		receiver.setParameter("gervil.program", "0:80");
		this.model.getProgram(0, 80).setReceiver(receiver);
		this.model.openChannel(CHANNEL).sendProgramChange(80);
		while (this.model.isBusy()) {
			Thread.yield();
		}
		this.blocks = new TGAudioBufferProcessor(this.model);
	}

	private TGSynthChannel channel() {
		return this.model.getChannelById(CHANNEL);
	}

	// the left side of the next blocks
	private float[] render(int count) {
		float[] left = new float[count * FRAMES];
		float[][] block = new float[TGAudioBuffer.CHANNELS][FRAMES];
		for (int b = 0; b < count; b++) {
			this.blocks.process();
			this.blocks.getBuffer().read(block);
			System.arraycopy(block[0], 0, left, b * FRAMES, FRAMES);
		}
		return left;
	}

	private static int firstSounding(float[] samples) {
		int first = 0;
		while (first < samples.length && Math.abs(samples[first]) <= SOUNDING) {
			first++;
		}
		return first;
	}

	// the frame the note sent untimed after the blocks starts sounding on
	private int untimedAfter(int blocks) throws MidiPlayerException {
		float[] before = render(blocks);
		channel().sendNoteOn(69, 127, 0, false);
		float[] after = render(BLOCKS);
		return before.length + firstSounding(after);
	}

	// Gervill's stream lags what it renders by two control periods (300 frames each at 44100 Hz): it opens with one of
	// silence it never rendered, and its limiter holds each period back one more to look ahead. A note sent before the
	// first block is rendered from Gervill's first frame and heard from frame 600 of the stream.
	@Test
	public void theStreamSoundsTwoControlPeriodsAfterGervillRenders() throws MidiPlayerException {
		channel().sendNoteOn(69, 127, 0, false);
		float[] left = render(BLOCKS);

		assertEquals(0, TestGervillProcessor.peak(Arrays.copyOfRange(left, 0, 600)), 0);
		assertTrue(TestGervillProcessor.peak(Arrays.copyOfRange(left, 600, 700)) > 0);
	}

	// as the player sends a note today: Gervill renders whole control periods as the stream is read, so after the first
	// block (512 frames of the stream) it has rendered its first period (heard from frame 600), and a note sent then is
	// rendered from its second period, 300 frames after one sent before the first block
	@Test
	public void anUntimedNoteSentAfterABlockStartsWithTheFirstControlPeriodNotRendered() throws MidiPlayerException {
		int early = untimedAfter(0);
		TestGervillTiming late = new TestGervillTiming();

		assertEquals(300, late.untimedAfter(1) - early);
	}
}
