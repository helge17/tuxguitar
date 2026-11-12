package app.tuxguitar.audio;

import java.util.Map;

import app.tuxguitar.song.models.TGTrack;

/**
 * What playing (or recording) sounded, offline: the timeline it played, each channel's tap (where it joins the output
 * mix, before Volume and Balance) and the output, left and right. Taps are mono for audio channels.
 */
public final class TGRender {

	private final TGTimeline timeline;
	private final Map<Integer, float[]> taps;
	private final float[][] output;

	public TGRender(TGTimeline timeline, Map<Integer, float[]> taps, float[][] output) {
		this.timeline = timeline;
		this.taps = taps;
		this.output = output;
	}

	public TGTimeline getTimeline() {
		return this.timeline;
	}

	// the tap of the track's channel; silence for a channel that sounded nothing
	public float[] getTap(TGTrack track) {
		return this.taps.getOrDefault(track.getChannelId(), new float[this.output[0].length]);
	}

	public float[] getLeft() {
		return this.output[0];
	}

	public float[] getRight() {
		return this.output[1];
	}
}
