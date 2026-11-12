package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins what TGRecording asks the audio device for: from which sample of its clock (counting from the start of the
 * song) and how many samples, at the measures' tempo and the Percentage of Tempo; and where song time puts the
 * measures it records.
 *
 * The song has 4 measures of 4/4 at 120 bpm (88200 samples each at 44100 Hz) and an audio track on input channel 2.
 * The device reads back each capture as "input:from+count".
 */
public class TestRecording {

	private static final int QUARTER = 960;
	private static final int MEASURE = 4 * QUARTER;

	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();
	private final TGTrack track;
	private final List<String> captures = new ArrayList<String>();

	private final TGAudioDevice device = new TGAudioDevice() {
		public int getSampleRate() {
			return 44100;
		}

		public float[] capture(int inputChannel, long from, int count) {
			captures.add(inputChannel + ":" + from + "+" + count);
			return new float[count];
		}
	};

	public TestRecording() {
		for (int m = 1; m < 4; m++) {
			songManager.addNewMeasureBeforeEnd(song);
		}
		track = songManager.addAudioTrack(song, TGAudioInput.mono(2));
	}

	// positions are in ticks from the start of measure 1
	private int record(long start, long stop, int percent, TGPunchRange punchRange) {
		long origin = song.getMeasureHeader(0).getStart();
		TGRecording recording = new TGRecording(songManager, song, device, punchRange, percent);
		return recording.record(List.of(track), TGSongTime.play(song, new MidiPlayerMode(), origin + start, origin + stop, 1));
	}

	@Test
	public void theDeviceIsReadFromTheStartPosition() {
		assertEquals(1, record(0, 2 * MEASURE, 100, null));
		assertEquals(List.of("2:0+176400"), captures);
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
}
