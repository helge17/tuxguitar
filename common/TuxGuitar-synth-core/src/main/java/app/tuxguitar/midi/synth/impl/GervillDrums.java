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

	// the click's note reaches the drums on the frame and is released length frames later; sent at least a block
	// before the drums fill that frame
	public void click(MidiClick click, long frame, long length) {
		this.drums.sendNoteOnAt(click.getNote(), click.getVelocity(), frame);
		this.drums.sendNoteOffAt(click.getNote(), click.getVelocity(), frame + length);
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
