package app.tuxguitar.audio;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * Renders a timeline of the song offline, on the audio device's sample clock: each channel's tap (an audio track's
 * takes, or the General MIDI program a track with notes plays, see TGMidiTaps) and the drum tap (the Count In, see
 * TGDrumTap), then the output mix, where each channel heard is scaled by its Volume and Balance (today's
 * constant-power law), and the drum tap by those of the song's percussion channel (a new channel's without one).
 * Muted tracks, and tracks other than the soloed ones, are not heard. With a MIDI Port other than the TuxGuitar Synthesizer nothing is heard here: audio tracks and the drums need
 * it, and the other tracks and today's Count In play on that port. Plugin delay compensation, when it comes, delays
 * each tap, the drum tap included, where it joins the mix.
 */
public final class TGRenderer {

	private final TGContext context;
	private final TGSongManager songManager;
	private final TGSong song;
	private final TGAudioSession session;
	private final TGTimeStretcher stretcher;

	public TGRenderer(TGContext context, TGSongManager songManager, TGSong song, TGAudioSession session) {
		this.context = context;
		this.songManager = songManager;
		this.song = song;
		this.session = session;
		this.stretcher = new TGWsolaStretcher(session.getDevice().getSampleRate());
	}

	// recording: the armed tracks record where the punch range lets them; synthesizer: the MIDI Port is the TuxGuitar
	// Synthesizer
	public TGRender render(TGTimeline timeline, MidiPlayerMode mode, boolean recording, boolean synthesizer) throws MidiPlayerException {
		Map<Integer, float[][]> taps = new HashMap<Integer, float[][]>();
		float[][] drums = new float[2][(int) timeline.length()];
		float[][] output = new float[2][(int) timeline.length()];
		if (synthesizer) {
			Iterator<TGTrack> tracks = this.song.getTracks();
			while (tracks.hasNext()) {
				TGTrack track = tracks.next();
				if (track.isAudio()) {
					taps.put(track.getChannelId(), this.tap(track, timeline, recording));
				}
			}
			taps.putAll(TGMidiTaps.render(this.context, this.songManager, this.song, mode, timeline, new HashSet<Integer>(taps.keySet())));
			drums = TGDrumTap.render(this.context, timeline, MidiPlayer.getInstance(this.context).getClick());
			this.mix(taps, drums, output);
		}
		return new TGRender(timeline, taps, drums, output);
	}

	private float[][] tap(TGTrack track, TGTimeline timeline, boolean recording) {
		TGTrackSource[] sources = this.sources(track, timeline, recording);
		float[][] input = (Arrays.asList(sources).contains(TGTrackSource.INPUT) ? this.input(track, timeline) : null);
		return TGTakeTap.render(track, timeline, this.stretcher, sources, input);
	}

	// what the track's input channel(s) deliver over the timeline
	private float[][] input(TGTrack track, TGTimeline timeline) {
		return this.session.getDevice().capture(this.channel(track).getAudioInput(), timeline.getClock(), (int) timeline.length());
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

	private void mix(Map<Integer, float[][]> taps, float[][] drums, float[][] output) {
		boolean solo = this.anySolo();
		Iterator<TGTrack> tracks = this.song.getTracks();
		while (tracks.hasNext()) {
			TGTrack track = tracks.next();
			float[][] tap = taps.get(track.getChannelId());
			if (tap != null && !track.isMute() && (!solo || track.isSolo())) {
				add(tap, gains(this.channel(track)), output);
			}
		}
		add(drums, gains(this.drumChannel()), output);
	}

	// the channel the drums click on today, the song's percussion channel; without one, they sound as a new channel
	private TGChannel drumChannel() {
		TGChannel percussion = this.songManager.getPercussionChannel(this.song);
		return (percussion != null ? percussion : this.songManager.getFactory().newChannel());
	}

	private static void add(float[][] tap, float[] gains, float[][] output) {
		for (int side = 0; side < output.length; side++) {
			for (int i = 0; i < output[side].length; i++) {
				output[side][i] += tap[side][i] * gains[side];
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
