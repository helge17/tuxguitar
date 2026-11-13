package app.tuxguitar.audio;

import java.util.Map;

import app.tuxguitar.song.models.TGTrack;

/**
 * What playing (or recording) sounded, offline: the timeline it played, each channel's tap (where it joins the output
 * mix, before Volume and Balance, left and right), the drum tap (the General MIDI drums Count In clicks on, where
 * they join the mix) and the output, left and right.
 */
public final class TGRender {

	private final TGTimeline timeline;
	private final Map<Integer, float[][]> taps;
	private final float[][] drums;
	private final float[][] output;

	public TGRender(TGTimeline timeline, Map<Integer, float[][]> taps, float[][] drums, float[][] output) {
		this.timeline = timeline;
		this.taps = taps;
		this.drums = drums;
		this.output = output;
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

	public float[] getLeft() {
		return this.output[0];
	}

	public float[] getRight() {
		return this.output[1];
	}
}
