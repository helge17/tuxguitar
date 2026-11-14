package app.tuxguitar.midi.synth.impl;

import java.util.HashMap;
import java.util.Map;

import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.player.base.MidiClick;
import app.tuxguitar.util.TGContext;

/**
 * The General MIDI drums Count In and the Metronome click on, outside every sound chain: Gervill's drum kit (bank 128,
 * program 0) at a sample rate, filled a block at a time on a clock of its own, the frames filled since it opened.
 */
public final class GervillDrums {

	// frames per block
	public static final int FRAMES = TGAudioBuffer.BUFFER_SIZE / 2;

	private final GervillProcessor drums;

	public GervillDrums(TGContext context, float sampleRate) {
		this.drums = new GervillProcessor(context);
		this.drums.setSampleRate(sampleRate);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("gervil.program", "128:0");
		this.drums.restoreParameters(parameters);
		while (this.drums.isBusy()) {
			Thread.yield();
		}
	}

	// the click's note reaches the drums on the frame, counted from the next fill, and is released length frames later
	public void click(MidiClick click, long frame, long length) {
		this.drums.sendNoteOn(click.getNote(), click.getVelocity(), 0, false, frame);
		this.drums.sendNoteOff(click.getNote(), click.getVelocity(), 0, false, frame + length);
	}

	// the next block, left and right
	public void fill(float[][] block) {
		TGAudioBuffer buffer = new TGAudioBuffer();
		this.drums.fillBuffer(buffer);
		buffer.read(block);
	}

	public void close() {
		this.drums.close();
	}
}
