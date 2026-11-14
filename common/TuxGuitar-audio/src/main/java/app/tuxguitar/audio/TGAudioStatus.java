package app.tuxguitar.audio;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * What an app shows of its audio, read at one moment (an app polls it): whether a recording is under way (its state,
 * a message key), whether Record can start one (a device, an armed track, no recording under way) and the input meter
 * of each armed track of the song open, in track order. Without a device nothing is metered.
 */
public final class TGAudioStatus {

	public static final String NO_DEVICE = "audio.status.no-device";
	public static final String NOT_RECORDING = "audio.status.not-recording";
	public static final String RECORDING = "audio.status.recording";

	// a meter's scale: from this level (and quieter) to 0 dBFS
	private static final double FLOOR = -60;

	private final String state;
	private final boolean canRecord;
	private final List<Meter> meters;

	private TGAudioStatus(String state, boolean canRecord, List<Meter> meters) {
		this.state = state;
		this.canRecord = canRecord;
		this.meters = meters;
	}

	public static TGAudioStatus of(TGContext context) {
		TGAudioSession session = TGAudioSession.getInstance(context);
		if (session.getDevice() == null) {
			return new TGAudioStatus(NO_DEVICE, false, Collections.<Meter>emptyList());
		}
		List<Meter> meters = session.getArmedTracks(TGDocumentManager.getInstance(context).getSong()).stream().map(track -> new Meter(track, session.getInputMeter(track))).collect(Collectors.toList());
		boolean recording = session.isRecording();
		return new TGAudioStatus(recording ? RECORDING : NOT_RECORDING, !recording && !meters.isEmpty(), meters);
	}

	public String getState() {
		return this.state;
	}

	public boolean canRecord() {
		return this.canRecord;
	}

	public List<Meter> getMeters() {
		return this.meters;
	}

	/**
	 * An armed track's input level: how full its meter is (0 to 100), the level ("-6.0 dBFS", "-inf dBFS" for silence)
	 * and whether it has clipped since its clip indicator was reset.
	 */
	public static final class Meter {

		private final TGTrack track;
		private final int percent;
		private final String level;
		private final boolean clipped;

		private Meter(TGTrack track, TGInputMeter meter) {
			double level = meter.getLevel();
			this.track = track;
			this.percent = (int) Math.round(Math.min(100, Math.max(0, (level - FLOOR) / -FLOOR * 100)));
			this.level = (Double.isInfinite(level) ? "-inf" : String.format(Locale.ROOT, "%.1f", level)) + " dBFS";
			this.clipped = meter.hasClipped();
		}

		public TGTrack getTrack() {
			return this.track;
		}

		public int getPercent() {
			return this.percent;
		}

		public String getLevel() {
			return this.level;
		}

		public boolean hasClipped() {
			return this.clipped;
		}
	}
}
