package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonUtil;

/**
 * What the audio features keep outside the song: the audio device, which tracks are armed, their monitoring and input
 * level, the punch range, the recording offset. It is not saved, not undoable, and changing it does not mark the song modified. One per
 * context.
 */
public final class TGAudioSession {

	// the MIDI Port of the TuxGuitar Synthesizer (desktop's TGSynthPortProvider), the only one audio tracks sound through
	public static final String SYNTHESIZER_PORT_KEY = "tuxguitar-synth.port";

	// the audio device delivers its input in blocks of this many samples
	private static final int BLOCK = 512;

	// tracks are armed as objects: a track brought back by undo is a new one, so it comes back not armed
	private final Set<TGTrack> armedTracks = new HashSet<TGTrack>();
	// kept while a track is disarmed and armed again
	private final Map<TGTrack, TGMonitoring> monitoring = new HashMap<TGTrack, TGMonitoring>();
	private final Map<TGTrack, TGInputMeter> meters = new HashMap<TGTrack, TGInputMeter>();
	private TGAudioDevice device;
	private TGPunchRange punchRange;
	// the song the punch range was set on: a song opened later has none
	private TGSong punchSong;
	// samples added to the input latency the audio device reports, for drivers that report it wrong
	private int recordingOffset;
	// the device's clock while TuxGuitar is stopped: how much input it has delivered
	private long listened;
	// a recording is under way (the recording runs on the audio thread, actions on another)
	private volatile boolean recording;

	private TGAudioSession() {
	}

	public static TGAudioSession getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGAudioSession.class.getName(), key -> new TGAudioSession());
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

	// the audio device delivered count more samples while TuxGuitar is stopped: each armed track's input channel(s)
	// feed its input meter
	public void listen(TGSongManager songManager, TGSong song, int count) {
		this.meter(songManager, song, this.listened, count);
		this.listened += count;
	}

	// what the audio device delivered over count samples of its clock from the given one: each armed track's input
	// channel(s) feed its input meter, a block at a time (while stopped, and while recording)
	public void meter(TGSongManager songManager, TGSong song, long from, int count) {
		for (int done = 0; done < count; done += BLOCK) {
			int frames = Math.min(BLOCK, count - done);
			for (TGTrack track : this.getArmedTracks(song)) {
				TGAudioInput input = songManager.getChannel(song, track.getChannelId()).getAudioInput();
				this.getInputMeter(track).deliver(this.device.capture(input, from + done, frames));
			}
		}
	}

	// the track's input level, at the audio device's rate
	public TGInputMeter getInputMeter(TGTrack track) {
		return this.meters.computeIfAbsent(track, key -> new TGInputMeter(this.device.getSampleRate()));
	}

	public int getRecordingOffset() {
		return this.recordingOffset;
	}

	public void setRecordingOffset(int samples) {
		this.recordingOffset = samples;
	}

	// how much later than the output's clock the input delivers what was played in time with the output: the output
	// latency, the plugin delay compensation of what is heard (the slowest sound chain's latency, see TGRender), the
	// input latency the device reports, and the recording offset
	public int getRoundTrip(int compensation) {
		return this.device.getOutputLatency() + compensation + this.device.getInputLatency() + this.recordingOffset;
	}

	public boolean isRecording() {
		return this.recording;
	}

	public void setRecording(boolean recording) {
		this.recording = recording;
	}

	public void setPunchRange(TGSong song, TGPunchRange punchRange) {
		this.punchSong = song;
		this.punchRange = punchRange;
	}

	// the punch range stays on its measure numbers and ends with the song; none once the song ends before it starts, and
	// none on another song (one opened since)
	public TGPunchRange getPunchRange(TGSong song) {
		if (this.punchRange == null || song != this.punchSong) {
			return null;
		}
		return this.punchRange.endingWith(song);
	}
}
