package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

/**
 * What the audio features keep outside the song: the audio device, which tracks are armed, the punch range. It is not
 * saved, not undoable, and changing it does not mark the song modified. One per context.
 */
public final class TGAudioSession {

	// the MIDI Port of the TuxGuitar Synthesizer (desktop's TGSynthPortProvider), the only one audio tracks sound through
	public static final String SYNTHESIZER_PORT_KEY = "tuxguitar-synth.port";

	// tracks are armed as objects: a track brought back by undo is a new one, so it comes back not armed
	private final Set<TGTrack> armedTracks = new HashSet<TGTrack>();
	// kept while a track is disarmed and armed again
	private final Map<TGTrack, TGMonitoring> monitoring = new HashMap<TGTrack, TGMonitoring>();
	private TGAudioDevice device;
	private TGPunchRange punchRange;

	private TGAudioSession() {
	}

	public static TGAudioSession getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGAudioSession.class.getName(), new TGSingletonFactory<TGAudioSession>() {
			public TGAudioSession createInstance(TGContext context) {
				return new TGAudioSession();
			}
		});
	}

	public TGAudioDevice getDevice() {
		return this.device;
	}

	public void setDevice(TGAudioDevice device) {
		this.device = device;
	}

	public void arm(TGTrack track) {
		this.armedTracks.add(track);
	}

	public void disarm(TGTrack track) {
		this.armedTracks.remove(track);
	}

	public boolean isArmed(TGTrack track) {
		return this.armedTracks.contains(track);
	}

	// the armed tracks of the song, in track order
	public List<TGTrack> getArmedTracks(TGSong song) {
		List<TGTrack> tracks = new ArrayList<TGTrack>();
		Iterator<TGTrack> it = song.getTracks();
		while (it.hasNext()) {
			TGTrack track = it.next();
			if (isArmed(track)) {
				tracks.add(track);
			}
		}
		return tracks;
	}

	public TGMonitoring getMonitoring(TGTrack track) {
		return this.monitoring.getOrDefault(track, TGMonitoring.OFF);
	}

	public void setMonitoring(TGTrack track, TGMonitoring monitoring) {
		this.monitoring.put(track, monitoring);
	}

	public void setPunchRange(TGPunchRange punchRange) {
		this.punchRange = punchRange;
	}

	// the punch range stays on its measure numbers and ends with the song; none once the song ends before it starts
	public TGPunchRange getPunchRange(TGSong song) {
		if (this.punchRange == null) {
			return null;
		}
		return this.punchRange.endingWith(song);
	}
}
