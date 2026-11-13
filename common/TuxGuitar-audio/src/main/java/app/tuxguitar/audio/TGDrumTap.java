package app.tuxguitar.audio;

import app.tuxguitar.midi.synth.impl.GervillDrums;
import app.tuxguitar.player.base.MidiClick;
import app.tuxguitar.util.TGContext;

/**
 * The drum tap: what the TuxGuitar Synthesizer's General MIDI drums sound over a timeline, outside every sound chain,
 * where they join the output mix, left and right. They click the Count In: each click's note reaches the drums on its
 * sample, is heard from it (Gervill's latency is taken out, see TGGervillBlocks) and is held a beat. (The Metronome
 * will click here too.)
 */
public final class TGDrumTap {

	private TGDrumTap() {
	}

	public static float[][] render(TGContext context, TGTimeline timeline, MidiClick click) {
		float[][] tap = new float[2][(int) timeline.length()];
		long[] clicks = timeline.getCountIn().positions();
		// no drums to open when nothing clicks
		if (clicks.length == 0) {
			return tap;
		}
		GervillDrums drums = new GervillDrums(context, timeline.getSampleRate());
		for (long position : clicks) {
			drums.click(click, position, timeline.getCountIn().hold());
		}
		float[][] block = new float[2][TGGervillBlocks.FRAMES];
		for (long start = 0; start < TGGervillBlocks.end(timeline); start += TGGervillBlocks.FRAMES) {
			drums.fill(block);
			TGGervillBlocks.place(block, tap, start - TGGervillBlocks.latency(timeline));
		}
		drums.close();
		return tap;
	}
}
