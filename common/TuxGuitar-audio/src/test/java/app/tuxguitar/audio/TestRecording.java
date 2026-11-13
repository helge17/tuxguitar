package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * Pins what TGRecording asks the audio device for: from which sample of its clock (counting from the start of the
 * song, placed later by the round trip) and how many samples, at the measures' tempo and the Percentage of Tempo; and
 * where song time puts the measures it records.
 *
 * The song has 4 measures of 4/4 at 120 bpm (88200 samples each at 44100 Hz) and an audio track on input channel 2.
 * The device reads back each capture as "input:from+count", and delivers a constant level of 0.5. The track is not
 * armed in the session unless a test arms it.
 */
public class TestRecording {

	private static final int QUARTER = 960;
	private static final int MEASURE = 4 * QUARTER;

	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();
	private final TGTrack track;
	private final List<String> captures = new ArrayList<String>();
	private final TGAudioSession session = TGAudioSession.getInstance(new TGContext());
	// the plugin delay compensation of what is heard while recording
	private int compensation;

	private final TGAudioDevice device = new TGAudioDevice() {
		public int getSampleRate() {
			return 44100;
		}

		public float[] capture(int inputChannel, long from, int count) {
			captures.add(inputChannel + ":" + from + "+" + count);
			float[] samples = new float[count];
			Arrays.fill(samples, 0.5f);
			return samples;
		}
	};

	public TestRecording() {
		for (int m = 1; m < 4; m++) {
			songManager.addNewMeasureBeforeEnd(song);
		}
		track = songManager.addAudioTrack(song, TGAudioInput.mono(2));
		session.setDevice(device);
	}

	// positions are in ticks from the start of measure 1
	private int record(long start, long stop, int percent, TGPunchRange punchRange) {
		long origin = song.getMeasureHeader(0).getStart();
		session.setPunchRange(punchRange);
		TGRecording recording = new TGRecording(songManager, song, session, compensation);
		MidiPlayerMode mode = new MidiPlayerMode();
		mode.setCurrentPercent(percent);
		return recording.record(List.of(track), new TGTimeline(song, TGSongTime.play(song, mode, origin + start, origin + stop, 1), device.getSampleRate()));
	}

	@Test
	public void theDeviceIsReadFromTheStartPosition() {
		assertEquals(1, record(0, 2 * MEASURE, 100, null));
		assertEquals(List.of("2:0+176400"), captures);
	}

	// a pair of input channels makes a stereo take: left from the first, right from the second
	@Test
	public void aPairOfInputChannelsIsReadSideBySide() {
		songManager.getChannel(song, track.getChannelId()).setAudioInput(TGAudioInput.stereo(3, 1));
		record(0, MEASURE, 100, null);

		assertEquals(List.of("3:0+88200", "1:0+88200"), captures);
		assertEquals(2, TGTakes.list(track).get(0).getTake().countChannels());
	}

	// what was played in time with the output reaches the input one round trip after the clock: the take is read that
	// much later, so it lands on the beat
	@Test
	public void theDeviceIsReadOneRoundTripLater() {
		session.setRecordingOffset(1388);
		record(MEASURE, 2 * MEASURE, 100, new TGPunchRange(2, 2));

		assertEquals(List.of("2:89588+88200"), captures);
	}

	// what is heard comes out of the output the plugin delay compensation later (the slowest chain's latency, Gervill's at
	// least): it is part of the round trip
	@Test
	public void theDeviceIsReadLaterByThePluginDelayCompensationOfWhatIsHeard() {
		compensation = 600;
		record(MEASURE, 2 * MEASURE, 100, null);

		assertEquals(List.of("2:88800+88200"), captures);
	}

	// while recording, an armed track's input meter is fed what its input delivered, a block at a time, whether or not
	// it is kept (here nothing is: the punch range is not reached), from one round trip after the clock as the takes
	@Test
	public void anArmedTracksInputIsMeteredWhileRecording() {
		session.arm(track);
		session.setRecordingOffset(100);
		record(0, MEASURE, 100, new TGPunchRange(3, 3));

		assertEquals(-6.02, session.getInputMeter(track).getLevel(), 0.01);
		assertEquals(List.of("2:100+512", "2:88164+136"), List.of(captures.get(0), captures.get(captures.size() - 1)));
	}

	@Test
	public void theClockCountsTheMeasuresAndBeatsBeforeTheStart() {
		record(2 * MEASURE + 2 * QUARTER, 3 * MEASURE, 100, null);

		assertEquals(List.of("2:220500+44100"), captures);
	}

	@Test
	public void aSlowerPercentageOfTempoLastsLonger() {
		record(MEASURE, 2 * MEASURE, 50, null);

		assertEquals(List.of("2:176400+176400"), captures);
	}

	@Test
	public void aSlowerTempoLastsLonger() {
		song.getMeasureHeader(0).getTempo().setQuarterValue(60);
		record(MEASURE, 2 * MEASURE, 100, null);

		assertEquals(List.of("2:176400+88200"), captures);
	}

	@Test
	public void outsideThePunchRangeTheDeviceIsNotKept() {
		record(0, 4 * MEASURE, 100, new TGPunchRange(2, 3));

		assertEquals(List.of("2:88200+176400"), captures);
	}

	@Test
	public void nothingPlayedRecordsNothing() {
		assertEquals(0, record(5 * MEASURE, 6 * MEASURE, 100, null));
		assertEquals(List.of(), captures);
	}

	@Test
	public void aStopInsideTheFirstMeasureEndsTheTakeThere() {
		record(0, 2 * QUARTER, 100, null);

		assertEquals("0-1920", span());
	}

	private String span() {
		long origin = song.getMeasureHeader(0).getStart();
		TGTakeSpan span = TGTakes.list(track).get(0);
		return (span.getStart() - origin) + "-" + (span.getEnd() - origin);
	}

	@Test
	public void aPunchRangeStartingInTheLastMeasureEndsWithTheSong() {
		TGPunchRange range = new TGPunchRange(4, 6).endingWith(song);

		assertEquals("4 to 4", range.getFirst() + " to " + range.getLast());
	}

	// "first sample @ tempo" of each clip of the take, measure by measure
	private String clips() {
		List<String> clips = new ArrayList<String>();
		for (int m = 0; m < 4; m++) {
			for (app.tuxguitar.song.models.TGTakeClip clip : track.getMeasure(m).getTakeClips()) {
				clips.add("m" + (m + 1) + ":" + clip.getFirstSample() + "@" + clip.getTempo());
			}
		}
		return String.join(" ", clips);
	}

	@Test
	public void eachClipKnowsWhereItsAudioStartsInTheTakeAndTheTempoItWasRecordedAt() {
		song.getMeasureHeader(1).getTempo().setQuarterValue(60);
		record(2 * QUARTER, 3 * MEASURE, 80, null);

		assertEquals("m1:0@96.0 m2:55125@48.0 m3:275625@96.0", clips());
		assertEquals(List.of("2:55125+385875"), captures);
	}
}
