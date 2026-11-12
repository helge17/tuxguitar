package app.tuxguitar.audio;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;

/**
 * Renders a timeline of the song offline, on the audio device's sample clock: each channel's tap, then the output mix,
 * where each channel heard is scaled by its Volume and Balance (today's constant-power law). Muted tracks, and tracks
 * other than the soloed ones, are not heard. With a MIDI Port other than the TuxGuitar Synthesizer nothing is heard
 * here: audio tracks need it, and the other tracks play on that port.
 */
public final class TGRenderer {

	private final TGSongManager songManager;
	private final TGSong song;
	private final TGAudioSession session;
	private final TGTimeStretcher stretcher;

	public TGRenderer(TGSongManager songManager, TGSong song, TGAudioSession session) {
		this.songManager = songManager;
		this.song = song;
		this.session = session;
		this.stretcher = new TGWsolaStretcher(session.getDevice().getSampleRate());
	}

	// recording: the armed tracks record where the punch range lets them; synthesizer: the MIDI Port is the TuxGuitar
	// Synthesizer
	public TGRender render(TGTimeline timeline, boolean recording, boolean synthesizer) {
		Map<Integer, float[]> taps = new HashMap<Integer, float[]>();
		float[][] output = new float[2][(int) timeline.length()];
		if (synthesizer) {
			Iterator<TGTrack> tracks = this.song.getTracks();
			while (tracks.hasNext()) {
				TGTrack track = tracks.next();
				if (track.isAudio()) {
					taps.put(track.getChannelId(), this.tap(track, timeline, recording));
				}
			}
			this.mix(taps, output);
		}
		return new TGRender(timeline, taps, output);
	}

	private float[] tap(TGTrack track, TGTimeline timeline, boolean recording) {
		TGTrackSource[] sources = this.sources(track, timeline, recording);
		float[] input = null;
		for (TGTrackSource source : sources) {
			if (source == TGTrackSource.INPUT && input == null) {
				int channel = this.channel(track).getAudioInput().getChannel();
				input = this.session.getDevice().capture(channel, timeline.getClock(), (int) timeline.length() + TGTakePiece.FADE);
			}
		}
		return TGTakeTap.render(track, timeline, this.stretcher, sources, input);
	}

	// where the track records it sounds its input (monitoring Auto or Always) or nothing (Off); with monitoring
	// Always an armed track sounds its input everywhere else too
	private TGTrackSource[] sources(TGTrack track, TGTimeline timeline, boolean recording) {
		boolean armed = this.session.isArmed(track);
		TGMonitoring monitoring = this.session.getMonitoring(track);
		TGPunchRange punchRange = this.session.getPunchRange(this.song);
		TGTrackSource[] sources = new TGTrackSource[timeline.count()];
		for (int i = 0; i < sources.length; i++) {
			boolean records = recording && armed && (punchRange == null || punchRange.contains(timeline.get(i).getHeader().getNumber()));
			if (records) {
				sources[i] = (monitoring == TGMonitoring.OFF ? TGTrackSource.SILENCE : TGTrackSource.INPUT);
			} else {
				sources[i] = (armed && monitoring == TGMonitoring.ALWAYS ? TGTrackSource.INPUT : TGTrackSource.TAKES);
			}
		}
		return sources;
	}

	private void mix(Map<Integer, float[]> taps, float[][] output) {
		boolean solo = this.anySolo();
		Iterator<TGTrack> tracks = this.song.getTracks();
		while (tracks.hasNext()) {
			TGTrack track = tracks.next();
			float[] tap = taps.get(track.getChannelId());
			if (tap != null && !track.isMute() && (!solo || track.isSolo())) {
				float[] gains = gains(this.channel(track));
				for (int i = 0; i < tap.length; i++) {
					output[0][i] += tap[i] * gains[0];
					output[1][i] += tap[i] * gains[1];
				}
			}
		}
	}

	private boolean anySolo() {
		Iterator<TGTrack> tracks = this.song.getTracks();
		while (tracks.hasNext()) {
			if (tracks.next().isSolo()) {
				return true;
			}
		}
		return false;
	}

	// Volume and Balance as the TuxGuitar Synthesizer applies them today: constant power, 0 full left, 127 full right
	static float[] gains(TGChannel channel) {
		float pan = (channel.getBalance() / 127f) * ((float) Math.PI / 2f);
		float gain = channel.getVolume() / 127f;
		return new float[] {gain * (float) Math.cos(pan), gain * (float) Math.sin(pan)};
	}

	private TGChannel channel(TGTrack track) {
		return this.songManager.getChannel(this.song, track.getChannelId());
	}
}
