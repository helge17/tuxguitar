package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.midi.synth.impl.GervillProcessor;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGPropertiesManager;

/**
 * Plugin delay compensation, offline: each tap is read its own chain's latency later, so the taps are together from
 * their samples, and the render says how much later the output sounds them: the slowest chain's latency, the General
 * MIDI drums' (Gervill's) counting whether or not they click.
 *
 * The song is one measure of 4/4 at 120 bpm whose guitar track plays General MIDI program 80.
 */
public class TestDelayCompensation {

	private final TGContext context = new TGContext();
	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();

	public TestDelayCompensation() {
		TGPropertiesManager.getInstance(this.context).setPropertiesFactory(MapProperties::new);
		this.songManager.getChannel(this.song, this.song.getTrack(0).getChannelId()).setProgram((short) 80);
	}

	private TGRender render(int sampleRate) throws MidiPlayerException {
		return render(sampleRate, true);
	}

	// synthesizer: the MIDI Port is the TuxGuitar Synthesizer
	private TGRender render(int sampleRate, boolean synthesizer) throws MidiPlayerException {
		TGAudioSession session = TGAudioSession.getInstance(this.context);
		session.setDevice(new SilentDevice() {
			public int getSampleRate() {
				return sampleRate;
			}
		});
		MidiPlayerMode mode = new MidiPlayerMode();
		List<TGPlayedMeasure> played = TGSongTime.play(this.song, mode, this.song.getMeasureHeader(0).getStart(), Long.MAX_VALUE, 1);
		return new TGRenderer(this.context, this.songManager, this.song, session).render(new TGTimeline(this.song, played, sampleRate), mode, false, synthesizer);
	}

	// Gervill's two control periods at the device's rate
	@Test
	public void aGeneralMidiTrackSoundsAsLateAsGervillAtTheDevicesRate() throws MidiPlayerException {
		assertEquals(GervillProcessor.latency(44100) + " " + GervillProcessor.latency(48000), render(44100).getLatency() + " " + render(48000).getLatency());
	}

	// with another MIDI Port nothing is heard here, so nothing is delayed
	@Test
	public void anotherMidiPortDelaysNothing() throws MidiPlayerException {
		assertEquals(0, render(44100, false).getLatency());
	}

	// an audio track alone: its takes have no latency, the drums Gervill's
	@Test
	public void theDrumsCountWithoutAGeneralMidiTrack() throws MidiPlayerException {
		this.songManager.addAudioTrack(this.song, TGAudioInput.mono(1));
		this.songManager.removeTrack(this.song, 1);
		assertEquals(GervillProcessor.latency(44100), render(44100).getLatency());
	}
}
