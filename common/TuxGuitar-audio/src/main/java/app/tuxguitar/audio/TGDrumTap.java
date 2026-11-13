package app.tuxguitar.audio;

import app.tuxguitar.midi.synth.impl.GervillDrums;
import app.tuxguitar.midi.synth.impl.GervillProcessor;
import app.tuxguitar.player.base.MidiClick;
import app.tuxguitar.util.TGContext;

/**
 * The drum tap: what the TuxGuitar Synthesizer's General MIDI drums sound over a timeline, outside every sound chain,
 * where they join the output mix, left and right. They click the Count In: each click's note reaches the drums on its
 * sample, is heard from it (Gervill's latency is taken out, see TGBlocks) and is held a beat. (The Metronome
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
		float[][] block = new float[2][TGBlocks.FRAMES];
		int latency = latency(timeline);
		for (long start = 0; start < timeline.length() + latency; start += TGBlocks.FRAMES) {
			drums.fill(block);
			TGBlocks.place(block, tap, start - latency);
		}
		drums.close();
		return tap;
	}

	// the drums' latency, Gervill's at the timeline's rate, whether or not they click
	public static int latency(TGTimeline timeline) {
		return GervillProcessor.latency(timeline.getSampleRate());
	}
}
