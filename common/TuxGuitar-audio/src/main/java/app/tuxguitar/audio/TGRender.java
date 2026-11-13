package app.tuxguitar.audio;

import java.util.Map;

import app.tuxguitar.song.models.TGTrack;

/**
 * What playing (or recording) sounded, offline: the timeline it played, each channel's tap (where it joins the output
 * mix, before Volume and Balance, left and right), the drum tap (the General MIDI drums Count In clicks on, where
 * they join the mix) and the output, left and right; and its latency, the plugin delay compensation: live, every tap
 * is delayed to the slowest sound chain (the General MIDI drums count, whether or not they click, and so does a muted
 * track), so the output sounds what it holds that many samples after the timeline's clock (and the audio device's
 * output latency after that). Offline each tap was read its own chain's latency later, so what is here is together on
 * the timeline's clock.
 */
public final class TGRender {

	private final TGTimeline timeline;
	private final Map<Integer, float[][]> taps;
	private final float[][] drums;
	private final float[][] output;
	private final int latency;
	private final int tail;
	private final TGMetronome metronome;

	// tail: the longest tail of the chains heard
	public TGRender(TGTimeline timeline, Map<Integer, float[][]> taps, float[][] drums, float[][] output, int latency, int tail) {
		this(timeline, taps, drums, output, latency, tail, TGMetronome.NONE);
	}

	// metronome: the Metronome's clicks on the drum tap
	public TGRender(TGTimeline timeline, Map<Integer, float[][]> taps, float[][] drums, float[][] output, int latency, int tail, TGMetronome metronome) {
		this.metronome = metronome;
		this.timeline = timeline;
		this.taps = taps;
		this.drums = drums;
		this.output = output;
		this.latency = latency;
		this.tail = tail;
	}

	public TGTimeline getTimeline() {
		return this.timeline;
	}

	// the left side of the tap of the track's channel, the one side of a mono tap
	public float[] getTap(TGTrack track) {
		return this.getTap(track, 0);
	}

	// a side of the tap of the track's channel (0 left, 1 right); silence for a channel that sounded nothing
	public float[] getTap(TGTrack track, int side) {
		float[][] tap = this.taps.get(track.getChannelId());
		return (tap != null ? tap[side] : new float[(int) this.timeline.length()]);
	}

	// a side of the drum tap (0 left, 1 right)
	public float[] getDrums(int side) {
		return this.drums[side];
	}

	// the Metronome's clicks it sounded (none with the Metronome off)
	public TGMetronome getMetronome() {
		return this.metronome;
	}

	public int getLatency() {
		return this.latency;
	}

	// where the player reports the song is after each block, live, with the audio device's output latency (see
	// TGReportedPositions)
	public long[] getReported(int outputLatency) {
		return TGReportedPositions.of(this.timeline, this.latency + outputLatency);
	}

	// how many samples the chains heard sound on after the last measure: the longest of their tails
	// (TGAudioProcessor.INFINITE_TAIL: for ever)
	public int getTail() {
		return this.tail;
	}

	public float[] getLeft() {
		return this.output[0];
	}

	public float[] getRight() {
		return this.output[1];
	}
}
