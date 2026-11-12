package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGPropertiesManager;

/**
 * A track with notes renders headless through the TuxGuitar Synthesizer's Gervill source, at the audio device's rate:
 * its tap (before Volume and Balance) sounds its note at its pitch from its beat, Volume and Balance scale it into the
 * output, and a muted track is not heard.
 *
 * The song is one measure of 4/4 at 120 bpm (2 seconds); its guitar track plays fret 5 on string 1 (A4, 440 Hz) as a
 * quarter note on beat 1, with General MIDI program 80 (a square lead, whose fundamental is clear).
 */
public class TestGeneralMidiRender {

	private final TGContext context = new TGContext();
	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();

	public TestGeneralMidiRender() {
		TGPropertiesManager.getInstance(this.context).setPropertiesFactory(MapProperties::new);
		TGMeasure measure = song.getTrack(0).getMeasure(0);
		TGBeat beat = songManager.getFactory().newBeat();
		beat.setStart(measure.getStart());
		TGNote note = songManager.getFactory().newNote();
		note.setValue(5);
		note.setString(1);
		beat.getVoice(0).addNote(note);
		measure.addBeat(beat);
		channel().setProgram((short) 80);
	}

	private TGChannel channel() {
		return songManager.getChannel(song, song.getTrack(0).getChannelId());
	}

	private TGRender render(int sampleRate) throws MidiPlayerException {
		TGAudioSession session = TGAudioSession.getInstance(this.context);
		session.setDevice(new TGAudioDevice() {
			public int getSampleRate() {
				return sampleRate;
			}

			public float[] capture(int inputChannel, long from, int count) {
				return new float[count];
			}
		});
		MidiPlayerMode mode = new MidiPlayerMode();
		long start = song.getMeasureHeader(0).getStart();
		TGTimeline timeline = new TGTimeline(song, TGSongTime.play(song, mode, start, Long.MAX_VALUE, 1), sampleRate);
		return new TGRenderer(this.context, this.songManager, this.song, session).render(timeline, mode, false, true);
	}

	// the frequency from 200 to 1000 Hz with the highest level: the fundamental of a square lead
	private static int strongest(float[] samples, int from, int to, int sampleRate) {
		int strongest = 0;
		for (int frequency = 200; frequency <= 1000; frequency++) {
			if (Meters.level(samples, from, to, frequency, sampleRate) > Meters.level(samples, from, to, strongest, sampleRate)) {
				strongest = frequency;
			}
		}
		return strongest;
	}

	@Test
	public void aNoteSoundsAtItsPitchFromItsBeat() throws MidiPlayerException {
		float[] tap = render(44100).getTap(song.getTrack(0));
		assertEquals(88200, tap.length);
		assertTrue(Meters.peak(tap, 0, 2048) > 0.01, "the note starts on its beat");
		assertTrue(Meters.peak(tap, 30000, 88200) < 0.01, "the quarter note is over by beat 2");
		assertEquals(440, strongest(tap, 2048, 20000, 44100), 4.4);
	}

	@Test
	public void atTheDevicesRateTheNoteKeepsItsPitch() throws MidiPlayerException {
		float[] tap = render(48000).getTap(song.getTrack(0));
		assertEquals(96000, tap.length);
		assertEquals(440, strongest(tap, 2048, 20000, 48000), 4.4);
	}

	@Test
	public void theOutputScalesTheTapByVolumeAndBalance() throws MidiPlayerException {
		channel().setBalance((short) 0);
		TGRender render = render(44100);
		float[] tap = render.getTap(song.getTrack(0));
		assertEquals(Meters.peak(tap, 0, 20000) * channel().getVolume() / 127f, Meters.peak(render.getLeft(), 0, 20000), 0.001);
		assertEquals(0, Meters.peak(render.getRight(), 0, 20000), 0.0001);
	}

	// as the player does today, the MIDI sequence leaves out a muted track's notes
	@Test
	public void aMutedTrackIsNotHeard() throws MidiPlayerException {
		song.getTrack(0).setMute(true);
		TGRender render = render(44100);
		assertEquals(0, Meters.peak(render.getTap(song.getTrack(0)), 0, 20000), 0);
		assertEquals(0, Meters.peak(render.getLeft(), 0, render.getLeft().length), 0);
	}
}
