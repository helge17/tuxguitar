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
		note(song.getTrack(0).getMeasure(0), 0);
		channel().setProgram((short) 80);
	}

	// fret 5 on string 1, a quarter note at the tick from the measure's start
	private void note(TGMeasure measure, long tick) {
		TGBeat beat = songManager.getFactory().newBeat();
		beat.setStart(measure.getStart() + tick);
		TGNote note = songManager.getFactory().newNote();
		note.setValue(5);
		note.setString(1);
		beat.getVoice(0).addNote(note);
		measure.addBeat(beat);
	}

	private int firstSound(float[] tap) {
		int first = 0;
		while (first < tap.length && Math.abs(tap[first]) <= 0.001) {
			first++;
		}
		return first;
	}

	private TGChannel channel() {
		return songManager.getChannel(song, song.getTrack(0).getChannelId());
	}

	private TGRender render(int sampleRate) throws MidiPlayerException {
		return render(sampleRate, 1);
	}

	// from measure 1, or another one
	private TGRender render(int sampleRate, int fromMeasure) throws MidiPlayerException {
		return render(sampleRate, fromMeasure, Long.MAX_VALUE);
	}

	// from the start of a measure until a tick is reached
	private TGRender render(int sampleRate, int fromMeasure, long stop) throws MidiPlayerException {
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
		long start = song.getMeasureHeader(fromMeasure - 1).getStart();
		TGTimeline timeline = new TGTimeline(song, TGSongTime.play(song, mode, start, stop, 1), sampleRate);
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

	// today's constant-power law: left and right scaled by volume / 127 times the cosine and sine of balance / 127 of a
	// quarter turn
	@Test
	public void theOutputScalesTheTapByVolumeAndBalance() throws MidiPlayerException {
		channel().setVolume((short) 64);
		channel().setBalance((short) 100);
		TGRender render = render(44100);
		float[] tap = render.getTap(song.getTrack(0));
		double pan = 100 / 127.0 * Math.PI / 2;
		for (int i : new int[] {0, 1000, 5000}) {
			assertEquals(tap[i] * 64 / 127.0 * Math.cos(pan), render.getLeft()[i], 0.00001, "left at " + i);
			assertEquals(tap[i] * 64 / 127.0 * Math.sin(pan), render.getRight()[i], 0.00001, "right at " + i);
		}
		assertTrue(Meters.peak(render.getRight(), 0, 20000) > 0.01);
	}

	// today the tap lags the timeline by Gervill's latency, two control periods (600 samples at 44100 Hz): a note on the
	// first sample is heard from sample 600
	@Test
	public void aNoteOnTheFirstSampleIsHeardTheLatencyLater() throws MidiPlayerException {
		float[] tap = render(44100).getTap(song.getTrack(0));

		assertEquals(0, Meters.peak(tap, 0, 600), 0);
		assertTrue(Meters.peak(tap, 600, 700) > 0);
	}

	// as the synthesizer does today, an event goes out at the start of the block of 512 samples it falls in
	@Test
	public void aNoteSoundsFromTheStartOfTheBlockOfItsBeat() throws MidiPlayerException {
		song.getTrack(0).getMeasure(0).clear();
		note(song.getTrack(0).getMeasure(0), 960);
		int first = firstSound(render(44100).getTap(song.getTrack(0)));
		assertTrue(first >= 22016 && first < 22016 + 512, "the note of beat 2 (sample 22050) sounds from " + first);
	}

	@Test
	public void theGeneralMidiProgramZeroSoundsToo() throws MidiPlayerException {
		channel().setProgram((short) 0);
		assertTrue(Meters.peak(render(44100).getTap(song.getTrack(0)), 0, 20000) > 0.01);
	}

	// at 48000 Hz and 120 bpm a tick is 25 samples: a note at tick 512 falls on sample 12800, the first of a block, so
	// it goes out with a note at tick 513 (sample 12825), not with the block before
	@Test
	public void aNoteOnTheFirstSampleOfABlockGoesOutWithThatBlock() throws MidiPlayerException {
		assertEquals(firstSoundOfANoteAt(513), firstSoundOfANoteAt(512));
	}

	private int firstSoundOfANoteAt(long tick) throws MidiPlayerException {
		song.getTrack(0).getMeasure(0).clear();
		note(song.getTrack(0).getMeasure(0), tick);
		return firstSound(render(48000).getTap(song.getTrack(0)));
	}

	// a song of two measures, a note in measure 2 only
	private void twoMeasures() {
		songManager.addNewMeasureBeforeEnd(song);
		song.getTrack(0).getMeasure(0).clear();
		note(song.getTrack(0).getMeasure(1), 0);
	}

	@Test
	public void aNoteSoundsOnlyInItsMeasure() throws MidiPlayerException {
		twoMeasures();
		float[] tap = render(44100).getTap(song.getTrack(0));
		assertEquals(88200, firstSound(tap), 512);
	}

	// the same note on beat 1 of measures 1 and 2: the one on the measure line goes out once, as loud as the other
	@Test
	public void aNoteOnAMeasureLineGoesOutOnce() throws MidiPlayerException {
		twoMeasures();
		note(song.getTrack(0).getMeasure(0), 0);
		float[] tap = render(44100).getTap(song.getTrack(0));
		float first = Meters.peak(tap, 0, 30000);
		assertTrue(first > 0.01);
		assertEquals(first, Meters.peak(tap, 88200, 118200), first * 0.05);
	}

	// a note sounds as loud whether or not the measure after it is played
	@Test
	public void aNoteSoundsAsLoudWhenTheMeasureAfterItPlays() throws MidiPlayerException {
		twoMeasures();
		note(song.getTrack(0).getMeasure(0), 0);
		float alone = Meters.peak(render(44100, 1, song.getMeasureHeader(1).getStart()).getTap(song.getTrack(0)), 0, 30000);
		assertTrue(alone > 0.01);
		assertEquals(alone, Meters.peak(render(44100).getTap(song.getTrack(0)), 0, 30000), alone * 0.05);
	}

	// playing stops where measure 2 starts: its note, on that tick, is not sent
	@Test
	public void aNoteWherePlayingStopsIsNotSent() throws MidiPlayerException {
		twoMeasures();
		float[] tap = render(44100, 1, song.getMeasureHeader(1).getStart()).getTap(song.getTrack(0));
		assertEquals(88200, tap.length);
		assertEquals(0, Meters.peak(tap, 0, tap.length), 0.001);
	}

	// the channel's program, sent before measure 1, is sent when playing starts later
	@Test
	public void playingFromALaterMeasureSoundsItsNotes() throws MidiPlayerException {
		twoMeasures();
		float[] tap = render(44100, 2).getTap(song.getTrack(0));
		assertEquals(0, firstSound(tap), 1024);
	}

	// repeats are unrolled: measure 1 played twice puts measure 2 third
	@Test
	public void aRepeatPutsTheMeasuresAfterItLater() throws MidiPlayerException {
		twoMeasures();
		song.getMeasureHeader(0).setRepeatOpen(true);
		song.getMeasureHeader(0).setRepeatClose(1);
		float[] tap = render(44100).getTap(song.getTrack(0));
		assertEquals(3 * 88200, tap.length);
		assertEquals(2 * 88200, firstSound(tap), 512);
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
